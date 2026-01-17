package org.spring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.*;
import org.spring.enums.GameStatus;
import org.spring.exc.GameCommonException;
import org.spring.service.GameService;
import org.spring.util.CardDefinitionUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Сервис управления игровой логикой.
 * <p>
 * Обеспечивает создание игр, управление колодами, покупку и розыгрыш карт,
 * обработку атак, завершение хода и другие игровые операции.
 * <p>
 * Игра хранится в виде объекта состояние игры {@link GameState} , который включает игроков {@link PlayerState},
 * рынок карт и текущий статус игры {@link GameStatus}.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final EffectServiceImpl effectService;

    /** Хранилище активных игр */
    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    @Override
    public GameState createGame(Room room, String creatorName) {
        String gameId = UUID.randomUUID().toString().substring(0, 8);
        GameState gs = new GameState(gameId);

        // Создание игроков
        for (String player : room.getPlayers()) {
            gs.getPlayers().put(player, new PlayerState(player));
        }

        games.put(gameId, gs);
        gs.setStatus(GameStatus.IN_PROGRESS);
        log.info("Создана новая игра с ID {}", gameId);

        initGame(gs);
        return gs;
    }

    @Override
    public void playCard(GameState gs, String playerId, String cardId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        CardInstance card = player.getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_HAND", "Такой карты нет в руке"));

        // Применяем способности карты
        effectService.applyPlayEffects(card,player,gs);

        // Перемещаем карту в стопку сброса
        player.getHand().remove(card);
        player.getPlayedCards().add(card);

        log.info("Игрок {} сыграл карту {}. Золото: {}, Атака: {}",
                playerId, card.getDefinition().getName(), player.getCurrentGold(), player.getCurrentAttack());
    }

    @Override
    public void scrapCard(GameState gs, String playerId, String cardId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        CardInstance card = player.getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_HAND", "Такой карты нет в руке"));

        if (card.getDefinition().getScrapEffects() == null || card.getDefinition().getScrapEffects().getEffects().isEmpty()) {
            throw new GameCommonException("EMPTY_EFFECT", "У карты нет сброса");
        }
        effectService.applyScrapEffects(card,player,gs);
        player.getHand().remove(card);

        log.info("Игрок {} сбросил карту {}", playerId, card.getDefinition().getName());
    }

    @Override
    public void buyCard(GameState gs, String playerId, String marketCardId) {
        PlayerState player = getPlayerOrThrow(gs, playerId);

        List<CardInstance> market = gs.getMarket();

        CardInstance card = market.stream()
                .filter(c -> c.getId().equals(marketCardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_MARKET", "Карты нет в магазине"));

        if (player.getCurrentGold() < card.getDefinition().getCost()) {
            throw new GameCommonException("NOT_ENOUGH_GOLD", "Недостаточно золота");
        }

        player.setCurrentGold(player.getCurrentGold() - card.getDefinition().getCost());
        player.getDiscardPile().add(card);

        market.remove(card);

        if (!gs.getMarketDeck().isEmpty()) {
            market.add(gs.getMarketDeck().removeFirst());
        }

        log.info("Игрок {} купил карту {}", playerId, card.getDefinition().getName());
    }

    @Override
    public void attack(GameState gs, String playerId) {
        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (!player.getHand().isEmpty()) {
            throw new GameCommonException("HAND_NOT_EMPTY", "Рука не пуста");
        }

        String opponentId = gs.getPlayers().keySet().stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("OPPONENT_NOT_FOUND", "Оппонент не найден"));

        PlayerState opponent = gs.getPlayers().get(opponentId);

        opponent.setHealth(opponent.getHealth() - player.getCurrentAttack());
        player.setCurrentAttack(0);

        log.info("Атака: игрок {} -> оппонент {}, здоровье оппонента {}", playerId, opponentId, opponent.getHealth());

        if (opponent.getHealth() <= 0) {
            gs.setStatus(GameStatus.FINISHED);
            gs.setWinnerId(playerId);
            log.info("Игра завершена. Победитель: {}", playerId);
        }
    }

    @Override
    public void endTurn(GameState gs, String playerId) {
        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (!player.getHand().isEmpty()) {
            throw new GameCommonException("HAND_NOT_EMPTY", "Рука не пуста");
        }

        player.setCurrentAttack(0);
        player.setCurrentGold(0);
        player.getDiscardPile().addAll(player.getPlayedCards());

        // Определяем следующего игрока
        String nextId = gs.getPlayers().keySet().stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("NEXT_PLAYER_NOT_FOUND", "Следующий игрок не найден"));

        gs.setActivePlayerId(nextId);

        PlayerState nextPlayer = gs.getPlayers().get(nextId);

        if (nextPlayer.getHand().isEmpty()) {
            drawCardsToHand(nextPlayer, 5);
        }

        log.info("Ход завершен для игрока {}", playerId);
    }

    /* ========================= UTIL ========================= */

    private void initGame(GameState gs) {
        log.info("Инициализация игры...");
        // Рынок
        List<CardInstance> marketDeck = CardDefinitionUtil.getCoreSet().stream()
                .flatMap(definition -> expand(definition).stream())
                .collect(Collectors.toList());
        Collections.shuffle(marketDeck);
        gs.getMarketDeck().clear();
        gs.getMarketDeck().addAll(marketDeck);

        // Колода пионеров
        CardDefinition explorer = CardDefinitionUtil.getExplorer();
        List<CardInstance> explorers = expand(explorer);
        gs.getExplorerPile().clear();
        gs.getExplorerPile().addAll(explorers);

        // Стартовые колоды игроков
        int playersCount = gs.getPlayers().size();
        CardDefinition scout = CardDefinitionUtil.getScout();
        CardDefinition viper = CardDefinitionUtil.getViper();
        for (PlayerState player : gs.getPlayers().values()) {
            List<CardInstance> deck = new ArrayList<>();
            for (int i = 0; i < scout.getCopies() / playersCount; i++)
                deck.add(new CardInstance(scout));
            for (int i = 0; i < viper.getCopies() / playersCount; i++)
                deck.add(new CardInstance(viper));
            Collections.shuffle(deck);
            player.setDeck(deck);
        }

        // Выложить первые 5 карт рынка
        gs.getMarket().clear();
        for (int i = 0; i < 5; i++) {
            gs.getMarket().add(gs.getMarketDeck().removeFirst());
        }

        // Первый активный игрок
        List<String> ids = new ArrayList<>(gs.getPlayers().keySet());
        gs.setActivePlayerId(ids.get(new Random().nextInt(ids.size())));

        // Первый активный игрок берет 3 карты, остальные игроки 5 карт
        for (PlayerState player : gs.getPlayers().values()) {
            if (gs.getActivePlayerId().equals(player.getPlayerId())){
                drawCardsToHand(player, 3);
            } else {
                drawCardsToHand(player, 5);
            }
        }

        log.info("Инициализация завершена. Первый ход: {}", gs.getActivePlayerId());
    }

    private void drawCardsToHand(PlayerState player, int n) {
        for (int i = 0; i < n; i++) {
            if (player.getDeck().isEmpty()) {
                Collections.shuffle(player.getDiscardPile());
                player.setDeck(new LinkedList<>(player.getDiscardPile()));
                player.getDiscardPile().clear();
            }
            if (player.getDeck().isEmpty()) break;
            player.getHand().add(player.getDeck().removeFirst());
        }
    }

    private List<CardInstance> expand (CardDefinition definition) {
        List<CardInstance> result = new ArrayList<>();
        for (int i = 0; i < definition.getCopies(); i++) {
            result.add(new CardInstance(definition));
        }
        return result;
    }

    private PlayerState getPlayerOrThrow(GameState gs, String playerId) {
        PlayerState player = gs.getPlayers().get(playerId);
        if (player == null)
            throw new GameCommonException("PLAYER_NOT_FOUND", "Игрок не найден");
        if (gs.getStatus() == GameStatus.FINISHED)
            throw new GameCommonException("GAME_FINISHED", "Игра окончена");
        return player;
    }

    @Override
    public Optional<GameState> findGame(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }
}