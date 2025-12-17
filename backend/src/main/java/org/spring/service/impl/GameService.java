package org.spring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.dto.Card;
import org.spring.dto.GameState;
import org.spring.dto.PlayerState;
import org.spring.enums.GameStatus;
import org.spring.exc.UserCommonException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервис управления игровой логикой.
 * <p>
 * Обеспечивает создание комнат, присоединение игроков, управление колодами, покупку и розыгрыш карт,
 * обработку атак, завершение хода и другие игровые операции.
 * <p>
 * Игровая комната хранится в виде объекта состояние игры {@link GameState} , который включает игроков {@link PlayerState},
 * рынок карт и текущий статус игры {@link GameStatus}.
 */
@Service
public class GameService {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    /** Хранилище активных игр по roomId */
    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    /** Генератор случайных чисел для выбора активного игрока */
    private final Random rnd = new Random();

    /**
     * Создаёт новую игровую комнату.
     *
     * @param creatorName ID создателя комнаты
     * @return объект {@link GameState} с созданной комнатой
     */
    public GameState createRoom(String creatorName) {
        String roomId = generateRoomId();
        GameState gs = new GameState(roomId);
        PlayerState p = new PlayerState(creatorName);
        gs.getPlayers().put(creatorName, p);
        gs.setStatus(GameStatus.WAITING_FOR_PLAYER);
        games.put(roomId, gs);
        log.info("Комната с ID {} создана", roomId);
        return gs;
    }

    /**
     * Находит игровую комнату по её ID.
     *
     * @param roomId ID комнаты
     * @return {@link Optional} с объектом {@link GameState} или пустой, если комната не найдена
     */
    public Optional<GameState> findRoom(String roomId) {
        return Optional.ofNullable(games.get(roomId));
    }

    /** Генерация уникального короткого ID комнаты */
    private String generateRoomId() {
        // короткий уникальный id
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Присоединяет игрока к существующей комнате.
     * <p>
     * Если все условия соблюдены (комната существует, не превышено число игроков),
     * инициирует игру через {@link #initGame(GameState)}.
     *
     * @param roomId   ID комнаты
     * @param playerId ID присоединяемого игрока
     * @throws UserCommonException если комната не найдена, игрок уже в комнате или комната заполнена
     */
    public synchronized void joinRoom(String roomId, String playerId) {
        GameState gs = games.get(roomId);
        if (gs == null) throw new UserCommonException(404, "Room not found");
        if (gs.getPlayers().containsKey(playerId)) throw new UserCommonException(409, "Player already joined");
        if (gs.getPlayers().size() >= 2) throw new UserCommonException(409, "Room full");

        PlayerState p = new PlayerState(playerId);
        gs.getPlayers().put(playerId, p);
        log.info("Игрок с ID {} добавлен в комнату с ID {}", playerId, roomId);

        gs.setStatus(GameStatus.IN_PROGRESS);
        initGame(gs);
    }

    /**
     * Инициализация игры:
     * <ul>
     *     <li>создание стартовых колод для игроков</li>
     *     <li>создание колоды магазина</li>
     *     <li>выбор 5 карт для рынка</li>
     *     <li>рандомное определение первого активного игрока</li>
     * </ul>
     *
     * @param gs объект {@link GameState} комнаты
     */
    private void initGame(GameState gs) {
        log.info("Инициализация игры...");
        // create start decks (each 10 cards: 8 gold, 2 attack)
        for (PlayerState p : gs.getPlayers().values()) {
            List<Card> deck = new ArrayList<>();
            for (int i = 0; i < 5; i++) deck.add(Card.goldCard());
            for (int i = 0; i < 5; i++) deck.add(Card.attackCard());
            Collections.shuffle(deck);
            p.setDeck(new LinkedList<>(deck));
            // draw 3 start cards
            drawCardsToHand(p, 3);
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
        gs.setActivePlayerId(ids.get(rnd.nextInt(ids.size())));
        log.info("Рандомно определен игрок для хода {}", gs.getActivePlayerId());
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
    public void drawCardsToHand(PlayerState p, int n) {
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

    /**
     * Игрок разыгрывает карту.
     *
     * @param gs       объект {@link GameState} комнаты
     * @param playerId ID игрока
     * @param cardId   ID карты
     * @throws IllegalStateException если карта не найдена или игрок отсутствует в комнате
     */
    public synchronized void playCard(GameState gs, String playerId, String cardId) {
        PlayerState player = gs.getPlayers().get(playerId);
        if (player == null) {
            throw new IllegalStateException("Player not in room");
        }
        Optional<Card> opt = player.getHand().stream().filter(c -> c.getId().equals(cardId)).findFirst();

        if (opt.isEmpty()) {
            throw new IllegalStateException("Card not in hand");
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

    /**
     * Игрок покупает карту из рынка.
     *
     * @param gs           объект {@link GameState} комнаты
     * @param playerId     ID игрока
     * @param marketCardId ID карты в рынке
     * @throws UserCommonException если игрок отсутствует, карта не найдена или недостаточно золота
     */
    public synchronized void buyCard(GameState gs, String playerId, String marketCardId) {

        PlayerState player = gs.getPlayers().get(playerId);

        if (player == null) {
            throw new UserCommonException(400, "Player not in room");
        }
        Optional<Card> opt = gs.getMarket().stream().filter(c -> c.getId().equals(marketCardId)).findFirst();

        if (opt.isEmpty()) {
            throw new UserCommonException(400, "Card not in market");
        }
        Card card = opt.get();

        if (player.getCurrentGold() < card.getCost()) {
            throw new UserCommonException(400, "Not enough gold");
        }

        player.setCurrentGold(player.getCurrentGold() - card.getCost());
        player.getDiscardPile().add(card);
        // замена из магазина карт
        int idx = gs.getMarket().indexOf(card);
        gs.getMarket().remove(idx);

        if (!gs.getMarketDeck().isEmpty()) {
            Card replacement = gs.getMarketDeck().removeFirst();
            gs.getMarket().add(idx, replacement);
        }
        log.info("Карта куплена");
        log.info("Карты в руке для игрока {}, {}", player.getPlayerId(), player.getHand());
    }

    /**
     * Игрок совершает атаку.
     *
     * @param gs       объект {@link GameState} комнаты
     * @param playerId ID игрока
     * @throws IllegalStateException если игрок не найден или противник отсутствует
     */
    public synchronized void attack(GameState gs, String playerId) {
        PlayerState player = gs.getPlayers().get(playerId);
        if (player == null) {
            throw new UserCommonException(400, "Player not in room");
        }

        String opponentId = gs.getPlayers().keySet()
                .stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElse(null);

        if (opponentId == null) {
            throw new UserCommonException(404, "No opponent");
        }

        PlayerState opponent = gs.getPlayers().get(opponentId);

        opponent.setHealth(opponent.getHealth() - player.getCurrentAttack());
        player.setCurrentAttack(0);

        if (opponent.getHealth() <= 0) {
            gs.setStatus(GameStatus.FINISHED);
            // winner = playerId, could store result
        }
        log.info("Атака совершена, ваше здоровье {}, здоровье противника {}", player.getHealth(), opponent.getHealth());
    }

    /**
     * Завершает ход игрока и передаёт ход следующему.
     *
     * @param gs       объект {@link GameState} комнаты
     * @param playerId ID игрока
     * @throws UserCommonException если игрок не найден или нет следующего игрока
     */
    public synchronized void endTurn(GameState gs, String playerId) {
        PlayerState player = gs.getPlayers().get(playerId);
        if (player == null) {
            throw new UserCommonException(400, "Player not in room");
        }
        // переместите руку и сыгранные карты, чтобы сбросить их.
        player.getDiscardPile().addAll(player.getHand());
        player.getDiscardPile().addAll(player.getPlayedCards());
        player.getHand().clear();
        player.getPlayedCards().clear();
        player.setCurrentAttack(0);
        player.setCurrentGold(0);

        // передача хода
        String next = gs.getPlayers()
                .keySet()
                .stream()
                .filter(id -> !id.equals(playerId))
                .findFirst().orElse(null);
        if (next == null) {
            throw new UserCommonException(400, "No next player");
        }
        gs.setActivePlayerId(next);

        // draw 5 for new active player
        PlayerState newActive = gs.getPlayers().get(next);
        drawCardsToHand(newActive, 5);
        log.info("Ход завершен для игрока {}", player.getPlayerId());
    }
}