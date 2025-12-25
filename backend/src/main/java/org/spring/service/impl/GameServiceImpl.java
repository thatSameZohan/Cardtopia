package org.spring.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.spring.dto.Card;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.dto.Room;
import org.spring.enums.GameStatus;
import org.spring.exc.GameCommonException;
import org.spring.service.GameService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
public class GameServiceImpl implements GameService {

    /** Хранилище активных игр по gameId */
    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    public GameState createGame (Room room, String creatorName) {

        String gameId =UUID.randomUUID().toString().substring(0, 8);
        GameState gs = new GameState(gameId);

        for (String player: room.getPlayers()){
            gs.getPlayers().put(player, new PlayerState(player));
        }

        games.put(gameId, gs);
        log.info("Игра с ID {} создана", gs.getId());
        initGame(gs);
        return gs;
    }

    public void playCard (GameState gs, String playerId, String cardId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        Card card = player.getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException ("CARD_NOT_IN_HAND", "Такой карты нет в руке"));

        player.getHand().remove(card);
        player.getPlayedCards().add(card);
        player.setCurrentAttack(player.getCurrentAttack() + card.getAttack());
        player.setCurrentGold(player.getCurrentGold() + card.getGold());

        // простой пример обработки возможностей
        if ("DRAW_1".equals(card.getAbility())) {
            drawCardsToHand(player, 1);
        }

        log.info("Игрок {} сыграл карту {}, текущее золото {}, атака {}", playerId, card.getId(), player.getCurrentGold(), player.getCurrentAttack());
    }

    public void buyCard (GameState gs, String playerId, String marketCardId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        Card card = gs.getMarket().stream()
                .filter(c -> c.getId().equals(marketCardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_MARKET", "Карты нет в магазине"));

        if (player.getCurrentGold() < card.getCost()) {
            throw new GameCommonException("NOT_ENOUGH_GOLD", "Недостаточно золота");
        }

        player.setCurrentGold(player.getCurrentGold() - card.getCost());

        player.getDeck().add(card); // добавляем купленную карту в колоду игрока


        int idx = gs.getMarket().indexOf(card);
        gs.getMarket().remove(idx); // удаляем купленную карту из магазина

        if (!gs.getMarketDeck().isEmpty()) {
            gs.getMarket().add(idx, gs.getMarketDeck().removeFirst()); // замена из магазина карт
        }
        log.info("Игрок {} купил карту {}", playerId, card.getId());
    }

    public void attack(GameState gs, String playerId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        String opponentId = gs.getPlayers().keySet()
                .stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("OPPONENT_NOT_FOUND", "Оппонент не найден"));

        PlayerState opponent = gs.getPlayers().get(opponentId);

        opponent.setHealth(opponent.getHealth() - player.getCurrentAttack()); // атака снимает хп с оппонента

        player.setCurrentAttack(0); // обнуляется атака для игрока

        if (opponent.getHealth() <= 0) {
            gs.setStatus(GameStatus.FINISHED);
            gs.setWinnerId(playerId);
            // в дальнейшем здесь можно сохранить результаты игры
            //save(gs);
            //games.remove(gs.getId());
        }
        log.info("Атака совершена, здоровье игрока {}: {}, оппонента {}: {}", playerId, player.getHealth(), opponentId, opponent.getHealth());
    }

    public void endTurn(GameState gs, String playerId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (!player.getHand().isEmpty()) {
            throw new GameCommonException("HAND_NOT_EMPTY", "Рука не пустая");
        }
        // переместите руку и сыгранные карты, чтобы сбросить их
        player.getDiscardPile().addAll(player.getPlayedCards());
        player.getHand().clear();
        player.getPlayedCards().clear();
        player.setCurrentAttack(0);
        player.setCurrentGold(0);

        // передача хода
        String next = gs.getPlayers().keySet().stream()
                .filter(id -> !gs.isPlayersTurn(id))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("NEXT_PLAYER_NOT_FOUND", "Следующий игрок не найден"));

        gs.setActivePlayerId(next);

        // взять 5 карт в руки следующему игроку
        PlayerState playerNext = gs.getPlayers().get(next);

        if (playerNext.getHand().isEmpty()) {
            drawCardsToHand(playerNext, 5);
        }

        log.info("Ход завершен для игрока {}", playerId);
    }

    public Optional<GameState> findGame (String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    /* ========================= UTIL ========================= */

    /**
     * Проверяем есть ли игрок и статус игры
     * @param gs игрок {@link GameState}
     * @param playerId имя игрока
     * @return {@link PlayerState}
     */
    private PlayerState getPlayerOrThrow(GameState gs, String playerId) {
        PlayerState player = gs.getPlayers().get(playerId);
        if (player == null) {
            throw new GameCommonException("PLAYER_NOT_FOUND", "Игрок не найден");
        }
        if (gs.getStatus() == GameStatus.FINISHED) {
            throw new GameCommonException("GAME_FINISHED", "Игра окончена");
        }
        return player;
    }

    /**
     * Инициализация игры:
     * <ul>
     *     <li>Создание стартовых колод для игроков</li>
     *     <li>Создание колоды магазина</li>
     *     <li>Выбор 5 карт для рынка</li>
     *     <li>Рандомное определение первого активного игрока</li>
     * </ul>
     *
     * @param gs объект {@link GameState} комнаты
     */
    private void initGame(GameState gs) {
        log.info("Инициализация игры...");
        // create start decks (each 10 cards: 5 gold, 5 attack)
        for (PlayerState p : gs.getPlayers().values()) {
            List<Card> deck = new ArrayList<>();
            for (int i = 0; i < 5; i++) deck.add(Card.goldCard());
            for (int i = 0; i < 5; i++) deck.add(Card.attackCard());
            Collections.shuffle(deck);
            p.setDeck(new LinkedList<>(deck));
        }
        log.info("Стартовые колоды созданы");
        // market deck (pool). Для простоты используем 30 карт смешанных
        List<Card> pool = new ArrayList<>();
        for (int i = 0; i < 20; i++) pool.add(Card.goldCard());
        for (int i = 0; i < 10; i++) pool.add(Card.attackCard());
        Collections.shuffle(pool);
        gs.getMarketDeck().clear();
        gs.getMarketDeck().addAll(pool);
        log.info("Магазин карт создан");
        // Взять 5 карт из магазина
        gs.getMarket().clear();
        for (int i = 0; i < 5; i++) {
            gs.getMarket().add(gs.getMarketDeck().removeFirst());
        }
        log.info("Взяты 5 карт из магазина: {}", gs.getMarket());

        // рандомное определение игрока для хода
        List<String> ids = new ArrayList<>(gs.getPlayers().keySet());
        gs.setActivePlayerId(ids.get(new Random().nextInt(ids.size())));
        for (PlayerState p: gs.getPlayers().values()) {
            drawCardsToHand(p,5);
        }
        log.info("Рандомно определен игрок для хода {}, все игроки взяли в руки 5 карт из своих колод", gs.getActivePlayerId());
        log.info("Инициализация игры завершена");
    }

    /**
     * Берёт n карт из колоды игрока и добавляет в руку.
     * <p>
     * Если колода пуста, перетасовывает сброшенные карты.
     *
     * @param p игрок {@link PlayerState}
     * @param n количество карт для взятия
     */
    private void drawCardsToHand(PlayerState p, int n) {
        for (int i = 0; i < n; i++) {
            if (p.getDeck().isEmpty()) {
                // перетасовка сброшенных предметов в колоду
                Collections.shuffle(p.getDiscardPile());
                p.setDeck(new LinkedList<>(p.getDiscardPile()));
                p.getDiscardPile().clear();
            }
            if (p.getDeck().isEmpty()) break; // no cards available
            p.getHand().add(p.getDeck().removeFirst());
        }
        log.info("Игрок {} взял {} карт", p.getPlayerId(),n);
    }
}