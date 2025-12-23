package org.spring.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.spring.dto.Card;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.dto.Room;
import org.spring.enums.GameStatus;
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

        if (isFinished(gs)){
            return;
        }

        PlayerState player = gs.getPlayers().get(playerId);

        if (player == null) {
            log.error("Такого игрока нет");
           return;
        }

        Optional<Card> opt = player.getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst();

        if (opt.isEmpty()) {
           log.error("Такой карты нет в руке у игрока {}",playerId);
           return;
        }

        Card card = opt.get();
        player.getHand().remove(card);
        player.getPlayedCards().add(card);
        player.setCurrentAttack(player.getCurrentAttack() + card.getAttack());
        player.setCurrentGold(player.getCurrentGold() + card.getGold());
        log.info("Карта сыграла");

        // простой пример обработки возможностей
        if ("DRAW_1".equals(card.getAbility())) {
            drawCardsToHand(player, 1);
        }

        log.info("Карты в руке для игрока {} {}. Текущее золото {}, текущая атака {}", player.getPlayerId(), player.getHand(), player.getCurrentGold(), player.getCurrentAttack());
    }

    public void buyCard (GameState gs, String playerId, String marketCardId) {

        if (isFinished(gs)){
            return;
        }

        PlayerState player = gs.getPlayers().get(playerId);

        if (player == null) {
            log.error("Игрок не существует");
            return;
        }

        Optional<Card> opt = gs.getMarket().stream().filter(c -> c.getId().equals(marketCardId)).findFirst();

        if (opt.isEmpty()) {
            log.error("Карты нет в магазине");
            return;
        }

        Card card = opt.get();

        if (player.getCurrentGold() < card.getCost()) {
            log.error("Недостаточно золота у игрока");
            return;
        }

        player.setCurrentGold(player.getCurrentGold() - card.getCost());

        player.getDeck().add(card); // добавляем купленную карту в колоду игрока


        int idx = gs.getMarket().indexOf(card);
        gs.getMarket().remove(idx); // удаляем купленную карту из магазина

        if (!gs.getMarketDeck().isEmpty()) {
            Card replacement = gs.getMarketDeck().removeFirst();
            gs.getMarket().add(idx, replacement); // замена из магазина карт
        }
        log.info("Карта куплена");
        log.info("Карты в руке для игрока {}, {}", player.getPlayerId(), player.getHand());
    }

    public void attack(GameState gs, String playerId) {

        if (isFinished(gs)){
            return;
        }

        PlayerState player = gs.getPlayers().get(playerId);

        if (player == null) {
            log.error("Игрок не существует");
            return;
        }

        String opponentId = gs.getPlayers().keySet()
                .stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElse(null);

        if (opponentId == null) {
            log.error("Такой оппонент не существует");
        }

        PlayerState opponent = gs.getPlayers().get(opponentId);

        opponent.setHealth(opponent.getHealth() - player.getCurrentAttack()); // атака снимает хп с оппонента

        player.setCurrentAttack(0); // обнуляется атака для игрока

        if (opponent.getHealth() <= 0) {
            gs.setStatus(GameStatus.FINISHED);
            gs.setWinnerId(playerId);
            // winner = playerId, в дальнейшем здесь можно сохранить результаты игры
//            save(gs);
//            games.remove(gs.getId());
        }
        log.info("Атака совершена, ваше здоровье {}, здоровье противника {}", player.getHealth(), opponent.getHealth());
    }

    public void endTurn(GameState gs, String playerId) {

        if (isFinished(gs)){
            return;
        }

        PlayerState player = gs.getPlayers().get(playerId);

        if (player == null) {
            log.error("Игрок не существует");
            return;
        }
        // переместите руку и сыгранные карты, чтобы сбросить их.
        player.getDiscardPile().addAll(player.getHand());
        player.getDiscardPile().addAll(player.getPlayedCards());
        player.getHand().clear();
        player.getPlayedCards().clear();
        player.setCurrentAttack(0);

        // передача хода
        String next = gs.getPlayers()
                .keySet()
                .stream()
                .filter(id -> !gs.isPlayersTurn(id))
                .findFirst().orElse(null);

        if (next == null) {
            log.error("Игрок для следующего хода не найден");
            return;
        }

        gs.setActivePlayerId(next);

        // взять 5 карт в руки следующему игроку
        PlayerState newActive = gs.getPlayers().get(next);
        drawCardsToHand(newActive, 5);
        log.info("Ход завершен для игрока {}", player.getPlayerId());
    }

    public Optional<GameState> findGame (String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    /* ========================= UTIL methods ========================= */

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
        drawCardsToHand (gs.getActivePlayer(), 5);
        log.info("Рандомно определен игрок для хода {}, который взял в руки 5 карт из колоды", gs.getActivePlayerId());
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
        log.info("Карты в руке для игрока {} {}", p.getPlayerId(), p.getHand());
    }

    private boolean isFinished(GameState gs) {
        if (gs.getStatus().equals(GameStatus.FINISHED)) {
            log.error("Игра окончена");
            return true;
        }
        return false;
    }
}