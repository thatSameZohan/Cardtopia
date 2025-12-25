package org.spring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.CardDto;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.dto.Room;
import org.spring.enums.GameStatus;
import org.spring.exc.GameCommonException;
import org.spring.mapper.CardMapper;
import org.spring.model.CardEntity;
import org.spring.repository.CardRepository;
import org.spring.service.GameService;
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

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private final AbilityService abilityService;

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
        log.info("Создана новая игра с ID {}", gameId);

        initGame(gs);
        return gs;
    }

    @Override
    public void playCard(GameState gs, String playerId, String cardId) {
        PlayerState player = getPlayerOrThrow(gs, playerId);

        CardDto card = player.getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_HAND", "Такой карты нет в руке"));

        // Перемещаем карту в playedCard
        player.getHand().remove(card);
        player.getPlayedCard().add(card);

        // Применяем способности карты
        abilityService.applyAbilities(card, player, gs, "PLAY");

        log.info("Игрок {} сыграл карту {}. Золото: {}, Атака: {}",
                playerId, card.getName(), player.getCurrentGold(), player.getCurrentAttack());
    }

    @Override
    public void buyCard(GameState gs, String playerId, String marketCardId) {
        PlayerState player = getPlayerOrThrow(gs, playerId);

        CardDto card = gs.getMarket().stream()
                .filter(c -> c.getId().equals(marketCardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_MARKET", "Карты нет в магазине"));

        if (player.getCurrentGold() < card.getCost()) {
            throw new GameCommonException("NOT_ENOUGH_GOLD", "Недостаточно золота");
        }

        player.setCurrentGold(player.getCurrentGold() - card.getCost());
        player.getDeck().add(card);

        int idx = gs.getMarket().indexOf(card);
        gs.getMarket().remove(idx);

        if (!gs.getMarketDeck().isEmpty()) {
            gs.getMarket().add(idx, gs.getMarketDeck().removeFirst());
        }

        // Применяем способности карты при покупке, если есть
//        abilityService.applyAbilitiesOnPurchase(card, player, gs);

        log.info("Игрок {} купил карту {}", playerId, card.getName());
    }

    @Override
    public void attack(GameState gs, String playerId) {
        PlayerState player = getPlayerOrThrow(gs, playerId);

        // Рассчитываем атаку с учетом способностей всех сыгранных карт
        int totalAttack = player.getPlayedCard().stream()
                .mapToInt(card -> abilityService.getAttackValue(card,player,gs))
                .sum();
        player.setCurrentAttack(totalAttack);

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

        // Сбрасываем сыгранные карты
        player.getDiscardPile().addAll(player.getPlayedCard());
        player.getPlayedCard().clear();
        player.setCurrentAttack(0);
        player.setCurrentGold(0);

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

        List<CardEntity> coreSet = cardRepository.findAllWithAbilities();

        List<CardEntity> personalDeckCards = coreSet.stream()
                .filter(c -> "Personal Deck".equals(c.getRole()))
                .toList();

        List<CardEntity> explorerCards = coreSet.stream()
                .filter(c -> "Explorer Pile".equals(c.getRole()))
                .toList();

        List<CardEntity> tradeDeckCards = coreSet.stream()
                .filter(c -> "Trade Deck".equals(c.getRole()))
                .toList();

        // Explorer Pile
        List<CardDto> explorers = explorerCards.stream()
                .flatMap(e -> expand(e).stream())
                .toList();
        gs.setExplorerPile(new ArrayDeque<>(explorers));

        // Market Deck
        List<CardDto> marketDeck = tradeDeckCards.stream()
                .flatMap(e -> expand(e).stream())
                .collect(Collectors.toList());
        Collections.shuffle(marketDeck);
        gs.getMarketDeck().clear();
        gs.getMarketDeck().addAll(marketDeck);

        // Personal Deck Map
        Map<String, CardEntity> personalMap = personalDeckCards.stream()
                .collect(Collectors.toMap(CardEntity::getName, c -> c));

        // Стартовые колоды игроков
        int playerCount = gs.getPlayers().size();
        for (PlayerState p : gs.getPlayers().values()) {
            List<CardDto> deck = new ArrayList<>();

            CardEntity scout = personalMap.get("Scout");
            CardEntity viper = personalMap.get("Viper");

            if (scout == null || viper == null) {
                throw new GameCommonException("CORE_SET", "Scout или Viper не найдены");
            }

            for (int i = 0; i < scout.getQty() / playerCount; i++)
                deck.add(cardMapper.fromEntity(scout));
            for (int i = 0; i < viper.getQty() / playerCount; i++)
                deck.add(cardMapper.fromEntity(viper));

            Collections.shuffle(deck);
            p.setDeck(deck);
        }

        // Выложить первые 5 карт рынка
        gs.getMarket().clear();
        for (int i = 0; i < 5; i++) {
            gs.getMarket().add(gs.getMarketDeck().removeFirst());
        }

        // Первый активный игрок
        List<String> ids = new ArrayList<>(gs.getPlayers().keySet());
        gs.setActivePlayerId(ids.get(new Random().nextInt(ids.size())));

        // Все игроки берут по 5 карт
        for (PlayerState p : gs.getPlayers().values()) {
            drawCardsToHand(p, 5);
        }

        log.info("Инициализация завершена. Первый ход: {}", gs.getActivePlayerId());
    }

    private void drawCardsToHand(PlayerState p, int n) {
        for (int i = 0; i < n; i++) {
            if (p.getDeck().isEmpty()) {
                Collections.shuffle(p.getDiscardPile());
                p.setDeck(new LinkedList<>(p.getDiscardPile()));
                p.getDiscardPile().clear();
            }
            if (p.getDeck().isEmpty()) break;
            p.getHand().add(p.getDeck().removeFirst());
        }
    }

    private List<CardDto> expand(CardEntity e) {
        List<CardDto> result = new ArrayList<>();
        for (int i = 0; i < e.getQty(); i++) {
            result.add(cardMapper.fromEntity(e));
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