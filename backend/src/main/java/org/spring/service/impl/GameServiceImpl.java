package org.spring.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.*;
import org.spring.enums.CardType;
import org.spring.enums.Faction;
import org.spring.enums.GameStatus;
import org.spring.exc.GameCommonException;
import org.spring.mapper.CardMapper;
import org.spring.service.GameService;
import org.spring.util.CardDefinitionUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.spring.enums.CardType.SHIP;

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
    private final CardMapper cardMapper;

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
    public void playCard(GameState gs, String playerId, PlayCardRequest req) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        ensureNotAwaitingDiscard(player);

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        CardInstance card = player.getHand().stream()
                .filter(c -> c.getId().equals(req.cardId()))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_HAND", "Такой карты нет в руке"));

        // Выбор фракции для карты Наемник
        if (card.getCode().equals("CORE_MERCENARY")) {

            if (req.faction() == null || req.faction() == Faction.NEUTRAL) {
                throw new GameCommonException("FACTION_REQUIRED", "Для этой карты необходимо выбрать фракцию");
            }

            card.setFaction(req.faction());
        }

        // Применяем способности карты
        effectService.applyPlayEffects(card,player,gs);

        // Применяем эффекты сброса карты
        if (req.scrap()) {
            effectService.applyScrapEffects(card,player,gs);
            log.info("Игрок {} удалил из игры карту {}", playerId, card.getName());
        } else {
            // Перемещаем карту в сыгранные
            player.getPlayedCards().add(card);
        }

        if (card.getType() == CardType.BASE) {
            player.getBases().add(card);
            player.getPlayedCards().remove(card);
        }

        if (card.getType() == CardType.OUTPOST) {
            player.getOutposts().add(card);
            player.getPlayedCards().remove(card);
        }
        player.getHand().remove(card);

        log.info("Игрок {} сыграл карту {}. Золото: {}, Атака: {}", playerId, card.getName(), player.getCurrentGold(), player.getCurrentAttack());
    }

    @Override
    public void buyCard(GameState gs, String playerId, String cardId, boolean topDeck) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        ensureNotAwaitingDiscard(player);

        CardInstance card;

        // 1. Пытаемся купить из рынка
        Optional<CardInstance> marketCard = gs.getMarket().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst();

        if (marketCard.isPresent()) {
            card = marketCard.get();
            buyFromMarket(gs, player, card, topDeck);
            return;
        }

        // 2. Пытаемся купить Explorer
        Optional<CardInstance> explorerCard = gs.getExplorerPile().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst();

        if (explorerCard.isPresent()) {
            card = explorerCard.get();
            buyFromExplorer(gs, player, card, topDeck);
            return;
        }

        throw new GameCommonException("CARD_NOT_AVAILABLE", "Карты нет ни в рынке, ни в стопке Explorer");
    }

    @Override
    public void attack(GameState gs, String playerId, AttackRequest req) {

        PlayerState attacker = getPlayerOrThrow(gs, playerId);

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        ensureNotAwaitingDiscard(attacker);

        if (!attacker.getHand().isEmpty()) {
            throw new GameCommonException("HAND_NOT_EMPTY", "Разыграйте все карты в руке");
        }

        String opponentId = gs.getPlayers().keySet().stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("OPPONENT_NOT_FOUND", "Оппонент не найден"));

        PlayerState opponent = gs.getPlayers().get(opponentId);

        int attack = attacker.getCurrentAttack();

        // 1 Есть аванпосты — атакуем их
        if (!opponent.getOutposts().isEmpty() || !attacker.getOutposts().isEmpty()) {
            if (!"OUTPOST".equals(req.targetType())) {
                throw new GameCommonException("OUTPOST_FIRST", "Сначала нужно уничтожить аванпост");
            }
            attackStructure(attacker, opponent, opponent.getOutposts(), req.targetId());
            return;
        }

        // 2️ База (по желанию)
        if ("BASE".equals(req.targetType())) {
            attackStructure(attacker, opponent, opponent.getBases(), req.targetId());
            return;
        }

        // 3️ Атака игрока
        opponent.setHealth(opponent.getHealth() - attack);
        attacker.setCurrentAttack(0);

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

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        ensureNotAwaitingDiscard(player);

        if (!player.getHand().isEmpty()) {
            throw new GameCommonException("HAND_NOT_EMPTY", "Разыграйте все карты в руке");
        }

//        if (player.getCurrentAttack() != 0){
//            throw new GameCommonException("MAKE_ATTACK", "Сначала совершите атаку");
//        }

        if (player.getBuyFreeTopDeck() > 0 && !gs.getMarket().isEmpty()) {
            throw new GameCommonException("REQUIRED_FREE_BUY", "Сначала совершите бесплатную покупку");
        }

        player.setCurrentGold(0);
        player.setCurrentAttack(0);
        player.setRightExile(0);
        player.setDestroyBase(0);
        player.setTopDeckNextShip(0);

        player.getPlayedCards().stream()
                .filter(c -> c.getCode().equals("CORE_MERCENARY"))
                .forEach(c -> c.setFaction(Faction.NEUTRAL));

        player.getDiscardPile().addAll(player.getPlayedCards());
        player.getPlayedCards().clear();

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

        activateStructures(nextPlayer, gs);

        log.info("Ход завершен для игрока {}", playerId);
    }

    @Override
    public void scrapStructure(GameState gs, String playerId, String cardId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        ensureNotAwaitingDiscard(player);

        CardInstance card = Stream.concat(
                        player.getBases().stream(),
                        player.getOutposts().stream()
                )
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("STRUCTURE_NOT_FOUND", "Структура не найдена"));

        effectService.applyScrapEffects(card, player, gs);

        player.getBases().remove(card);
        player.getOutposts().remove(card);
        player.getDiscardPile().add(card);
    }

    @Override
    public void exileCard(GameState gs, String playerId, String cardId, String cardCode) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        ensureNotAwaitingDiscard(player);

        if (player.getRightExile() <= 0) {
            throw new GameCommonException("NO_EXILE_RIGHT", "У вас нет права удалить карту");
        }

        CardInstance card;

        // КОСТЫЛЬ если носорог или улитка, удаляем из рынка
        if (cardCode.equals("CORE_RHINO") || cardCode.equals("CORE_SNAIL")) {
            card = gs.getMarket().stream()
                    .filter(c -> c.getId().equals(cardId))
                    .findFirst()
                    .orElseThrow(() -> new GameCommonException("CARD_NOT_FOUND", "Карта не найдена на рынке"));
            gs.getMarket().remove(card);
        // в остальных случаях удаляем из руки или сброса
        } else {
            card = Stream.of(player.getHand(), player.getDiscardPile())
                    .flatMap(List::stream)
                    .filter(c -> c.getId().equals(cardId))
                    .findFirst()
                    .orElseThrow(() -> new GameCommonException("CARD_NOT_FOUND", "Карта не найдена в руке или сбросе игрока"));
            player.getHand().remove(card);
            player.getDiscardPile().remove(card);
        }
        player.setRightExile(player.getRightExile() - 1);
        log.info("Игрок {} удалил из игры карту {}", playerId, card.getName());
    }

    public void forceDiscard(GameState gs,String playerId, String cardId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (player.getForcedDiscard() <= 0){
            throw new GameCommonException("FORCED_DISCARD_EMPTY", "Принудительный сброс не нужен");
        }

        CardInstance card = player.getHand().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_HAND", "Карта не найдена в руке"));

        player.getHand().remove(card);
        player.getPlayedCards().add(card);

        if (player.getForcedDiscard() > 0) {
            player.setForcedDiscard(player.getForcedDiscard() - 1);
        } else {
            player.setForcedDiscard(0);
        }

        log.info("Игрок {} принудительно сбросил карту {}", playerId, card.getName());
    }

    @Override
    public void destroyBase(GameState gs, String playerId, String baseId) {

        PlayerState attacker = getPlayerOrThrow(gs, playerId);

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        if (attacker.getDestroyBase() <= 0) {
            throw new GameCommonException("NO_DESTROY_RIGHT", "Нет права разрушить базу");
        }

        String opponentId = gs.getPlayers().keySet().stream()
                .filter(id -> !id.equals(playerId))
                .findFirst()
                .orElseThrow(() ->
                        new GameCommonException("OPPONENT_NOT_FOUND", "Оппонент не найден")
                );

        PlayerState opponent = gs.getPlayers().get(opponentId);

        CardInstance base = opponent.getBases().stream()
                .filter(c -> c.getId().equals(baseId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("BASE_NOT_FOUND", "База не найдена"));

        opponent.getBases().remove(base);
        opponent.getDiscardPile().add(base);

        attacker.setDestroyBase(attacker.getDestroyBase() - 1);

        log.info("Игрок {} разрушил базу {} игрока {}", playerId, base.getName(), opponentId);
    }

    @Override
    public void buyFreeTopDeck(GameState gs, String playerId, String cardId) {

        PlayerState player = getPlayerOrThrow(gs, playerId);

        if (!gs.isPlayersTurn(playerId)) {
            throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
        }

        if (player.getBuyFreeTopDeck() <= 0) {
            throw new GameCommonException("NO_FREE_SHIP_BUY", "Нет права на бесплатную покупку корабля");
        }

        CardInstance card = gs.getMarket().stream()
                .filter(c -> c.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("CARD_NOT_IN_MARKET", "Карты нет в рынке"));

        if (card.getType() != CardType.SHIP) {
            throw new GameCommonException("NOT_A_SHIP", "Можно купить только корабль");
        }

        // бесплатно → золото не трогаем
        gs.getMarket().remove(card);

        // кладём на верх колоды
        player.getDeck().addFirst(card);

        // пополняем рынок
        if (!gs.getMarketDeck().isEmpty()) {
            gs.getMarket().add(gs.getMarketDeck().removeFirst());
        }

        // уменьшаем счётчик
        player.setBuyFreeTopDeck(player.getBuyFreeTopDeck() - 1);

        log.info("Игрок {} бесплатно купил корабль {} и положил на верх колоды", playerId, card.getName());
    }


    @Override
    public Optional<GameState> findGame(String gameId) {
        return Optional.ofNullable(games.get(gameId));
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
                deck.add(cardMapper.toInstance(scout));
            for (int i = 0; i < viper.getCopies() / playersCount; i++)
                deck.add(cardMapper.toInstance(viper));
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
            result.add(cardMapper.toInstance(definition));
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

    private void buyFromMarket(GameState gs, PlayerState player, CardInstance card, boolean topDeck) {

        int cost = card.getCost();

        if (player.getCurrentGold() < cost) {
            throw new GameCommonException("NOT_ENOUGH_GOLD", "Недостаточно золота");
        }

        if (topDeck && player.getTopDeckNextShip() <= 0) {
            throw new GameCommonException("NOT_ENOUGH_TOP_DECK", "Нет прав положить карту наверх колоды");
        }

        player.setCurrentGold(player.getCurrentGold() - cost);

        if (player.getTopDeckNextShip() > 0 && topDeck && card.getType() == SHIP){
            player.getDeck().addFirst(card);
            player.setTopDeckNextShip(player.getTopDeckNextShip() - 1);

            log.info("Купленная карта {} положена на верх колоды", card.getName());
        } else {
            player.getDiscardPile().add(card);
            log.info("Купленная карта {} положена в стопку сброса", card.getName());
        }

        gs.getMarket().remove(card);

        // добираем рынок
        if (!gs.getMarketDeck().isEmpty()) {
            gs.getMarket().add(gs.getMarketDeck().removeFirst());
        }

        log.info("Игрок {} купил карту {} из рынка", player.getPlayerId(), card.getName());
    }

    private void buyFromExplorer(GameState gs, PlayerState player, CardInstance card, boolean topDeck) {

        int cost = card.getCost();

        if (player.getCurrentGold() < cost) {
            throw new GameCommonException("NOT_ENOUGH_GOLD", "Недостаточно золота");
        }

        if (topDeck && player.getTopDeckNextShip() <= 0) {
            throw new GameCommonException("NOT_ENOUGH_TOP_DECK", "Нет прав положить карту наверх колоды");
        }

        player.setCurrentGold(player.getCurrentGold() - cost);

        if (player.getTopDeckNextShip() > 0 && topDeck && card.getType() == SHIP){
            player.getDeck().addFirst(card);
            player.setTopDeckNextShip(player.getTopDeckNextShip() - 1);

            log.info("Купленная карта {} положена на верх колоды", card.getName());
        } else {
            gs.getExplorerPile().remove(card);
            log.info("Купленная карта {} положена в стопку сброса", card.getName());
        }

        log.info("Игрок {} купил карту Explorer {}", player.getPlayerId(), card.getName());
    }

    private void activateStructures(PlayerState player, GameState gs) {
        player.getBases().forEach(c ->
                effectService.applyPlayEffects(c, player, gs)
        );
        player.getOutposts().forEach(c ->
                effectService.applyPlayEffects(c, player, gs)
        );
    }

    private void attackStructure(PlayerState attacker,
                                 PlayerState opponent,
                                 List<CardInstance> structures,
                                 String targetId) {

        CardInstance target = structures.stream()
                .filter(c -> c.getId().equals(targetId))
                .findFirst()
                .orElseThrow(() -> new GameCommonException("TARGET_NOT_FOUND", "Цель не найдена"));

        int defense = target.getDefense();

        if (attacker.getCurrentAttack() < defense) {
            throw new GameCommonException("LOW_ATTACK","Недостаточно атаки");
        }

        attacker.setCurrentAttack(attacker.getCurrentAttack() - defense);
        structures.remove(target);
        opponent.getDiscardPile().add(target);
    }

    private void ensureNotAwaitingDiscard (PlayerState player) {
        if (player.getForcedDiscard() != 0 && !player.getHand().isEmpty()) {
            throw new GameCommonException("FORCED_DISCARD_REQUIRED", "Вы должны сбросить карту");
        }
    }
}