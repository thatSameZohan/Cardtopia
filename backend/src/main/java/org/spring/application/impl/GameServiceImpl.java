package org.spring.application.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.domain.card.*;
import org.spring.domain.game.CardActionService;
import org.spring.domain.game.CombatService;
import org.spring.domain.game.GameStatus;
import org.spring.domain.game.TurnService;
import org.spring.domain.market.MarketService;
import org.spring.domain.market.PurchaseType;
import org.spring.domain.validators.GameActionValidator;
import org.spring.dto.*;
import org.spring.exc.GameCommonException;
import org.spring.application.GameService;
import org.spring.domain.game.GameInitializer;
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
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final MarketService marketService;
    private final GameInitializer  gameInitializer;
    private final GameActionValidator gameActionValidator;
    private final CardActionService cardActionService;
    private final TurnService  turnService;
    private final CombatService combatService;

    /** Хранилище активных игр */
    private final Map<String, GameState> games = new ConcurrentHashMap<>();

    @Override
    public GameState createGame(Room room, String creatorName) {

        GameState gs = new GameState();

        // Создание игроков
        for (String player : room.getPlayers()) {
            gs.getPlayers().put(player, new PlayerState(player));
        }

        games.put(gs.getId(), gs);
        gs.setStatus(GameStatus.IN_PROGRESS);
        log.info("Создана новая игра с ID {}", gs.getId());

        gameInitializer.init(gs);
        return gs;
    }

    @Override
    public void playCard(GameState gs, String playerId, PlayCardRequest req) {

        PlayerState player = gs.getPlayer(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNeedForcedDiscard(player);

        cardActionService.play(gs, player, req);
    }

    @Override
    public void buyCard(GameState gs, String playerId, String cardId, PurchaseType type) {

        PlayerState player = gs.getPlayer(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNeedForcedDiscard(player);

        marketService.buyCard(gs, player, cardId, type);
    }

    @Override
    public void attack(GameState gs, String playerId, AttackRequest req) {

        PlayerState player = gs.getPlayer(playerId);

        PlayerState opponent = gs.getOpponent(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNeedForcedDiscard(player);

        gameActionValidator.validateEmptyHand(player);

        combatService.attack(gs,player,opponent,req);
    }

    @Override
    public void endTurn(GameState gs, String playerId) {

        PlayerState player = gs.getPlayer(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNeedForcedDiscard(player);

        gameActionValidator.validateEmptyHand(player);

//        gameActionValidator.validateMakeAttack(player);

        gameActionValidator.validateRequiredFreeBuy(player,gs);

        turnService.endTurn(gs,player);
    }

    @Override
    public void scrapStructure(GameState gs, String playerId, String cardId) {

        PlayerState player = gs.getPlayer(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNeedForcedDiscard(player);

        cardActionService.scrapStructure(gs,player,cardId);
    }

    @Override
    public void exileCard(GameState gs, String playerId, String cardId, CardCode cardCode) {

        PlayerState player = gs.getPlayer(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNeedForcedDiscard(player);

        gameActionValidator.validateRightExile(player);

        cardActionService.exile(gs, player,cardId,cardCode);
    }

    public void forceDiscard(GameState gs,String playerId, String cardId) {

        PlayerState player = gs.getPlayer(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNoNeedForcedDiscard(player);

        cardActionService.forceDiscard(player, cardId);
    }

    @Override
    public void destroyBase(GameState gs, String playerId, String baseId) {

        PlayerState player = gs.getPlayer(playerId);

        PlayerState opponent = gs.getOpponent(playerId);

        gameActionValidator.validateGameStatus(gs);

        gameActionValidator.validatePlayerTurn(gs, playerId);

        gameActionValidator.validateNeedForcedDiscard(player);

        gameActionValidator.validateRightDestroyBase(player);

        combatService.destroyBase(player, opponent, baseId);
    }

    @Override
    public GameState findGame(String gameId) {
        GameState game= games.get(gameId);
        if (game == null) {
            throw new GameCommonException("GAME_NOT_FOUND", "Игра не найдена");
        }
        return game;
    }
}