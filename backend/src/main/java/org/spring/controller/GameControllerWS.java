package org.spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.*;
import org.spring.exc.GameCommonException;
import org.spring.exc.RoomCommonException;
import org.spring.mapper.ViewMapper;
import org.spring.service.GameService;
import org.spring.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GameControllerWS {

    private final GameService gameService;
    private final RoomService roomService;
    private final SimpMessagingTemplate template;
    private final ViewMapper viewMapper;
    private final Map<String, Object> gameLocks = new ConcurrentHashMap<>();

    /* ========================= WebSocket API ========================= */

    @MessageMapping("/game.start")
    public void startGame (@Payload StartGameRequest req, Principal principal) {

        requireAuth(principal);

        GameState gs = roomService.startGame(req.roomId(), principal.getName());
        broadcastState(gs);

        template.convertAndSendToUser(principal.getName(), "/queue/game.started", Map.of(
                "roomId", req.roomId(),
                "gameId", gs.getId()
        ));
        template.convertAndSend("/topic/game.start." + req.roomId(), Map.of(
                "gameId", gs.getId(),
                "message", "start"
        ));
    }

    @MessageMapping("/game.init")
    public void initGame(@Payload InitGameRequest req, Principal principal) {

        GameState gs = validateAndGetGame(req.gameId(), principal);

        broadcastState(gs);
    }

    @MessageMapping("/game.playCard")
    public void playCard(@Payload PlayCardRequest req, Principal principal) {

        GameState gs = validateAndGetGame(req.gameId(), principal);

        synchronized (lockFor(gs.getId())) {
            if (!gs.isPlayersTurn(principal.getName())) {
                throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
            }
            gameService.playCard(gs, principal.getName(), req.cardId());
            broadcastState(gs);
        }
    }

    @MessageMapping("/game.scrapCard")
    public void scrapCard(@Payload PlayCardRequest req, Principal principal) {

        GameState gs = validateAndGetGame(req.gameId(), principal);

        synchronized (lockFor(gs.getId())) {
            if (!gs.isPlayersTurn(principal.getName())) {
                throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
            }
            gameService.scrapCard(gs, principal.getName(), req.cardId());
            broadcastState(gs);
        }
    }

    @MessageMapping("/game.buyCard")
    public void buyCard(BuyCardRequest req, Principal principal) {

        GameState gs = validateAndGetGame(req.gameId(), principal);

        synchronized (lockFor(gs.getId())) {
            if (!gs.isPlayersTurn(principal.getName())) {
                throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
            }
            gameService.buyCard(gs, principal.getName(), req.marketCardId());
            broadcastState(gs);
        }
    }


    @MessageMapping("/game.attack")
    public void attack(AttackRequest req, Principal principal) {

        GameState gs = validateAndGetGame(req.gameId(), principal);

        synchronized (lockFor(gs.getId())) {
            if (!gs.isPlayersTurn(principal.getName())) {
                throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
            }
            gameService.attack(gs, principal.getName());
            broadcastState(gs);
        }
    }

    @MessageMapping("/game.endTurn")
    public void endTurn(EndTurnRequest req, Principal principal) {

        GameState gs = validateAndGetGame(req.gameId(), principal);

        synchronized (lockFor(gs.getId())) {
            if (!gs.isPlayersTurn(principal.getName())) {
                throw new GameCommonException("NOT_YOUR_TURN", "Не ваш ход");
            }
            gameService.endTurn(gs, principal.getName());
            broadcastState(gs);
        }
    }

    /* ========================= UTIL ========================= */

    private Object lockFor(String gameId) {
        return gameLocks.computeIfAbsent(gameId, id -> new Object());
    }

    /**
     * Отправка состояния игры общее и приватное
     */
    private void broadcastState(GameState gs) {
        template.convertAndSend("/topic/game." + gs.getId(), viewMapper.toGameView(gs));
        gs.getPlayers().forEach((username, player) ->
                template.convertAndSendToUser(username, "/queue/game." + gs.getId(), viewMapper.toPrivatePlayerView(player))
        );
    }

    /**
     * Проверка авторизации
     */
    private void requireAuth(Principal principal) {
        if (principal == null) {
            throw new GameCommonException("UNAUTHORIZED", "Пользователь не авторизован");
        }
    }

    private GameState validateAndGetGame(String gameId, Principal principal) {
        requireAuth(principal);
        return gameService.findGame(gameId)
                .orElseThrow(() -> new GameCommonException("GAME_NOT_FOUND", "Игра не найдена"));
    }

    /* ========================= Exception Handling ========================= */

    @MessageExceptionHandler(GameCommonException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleGameException(GameCommonException exc) {
        log.error("Ошибка игры: {}", exc.getMessage());
        return new ErrorResponse(exc.getCode(), exc.getMessage());
    }

    @MessageExceptionHandler(RoomCommonException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleRoomException(RoomCommonException exc) {
        log.error("Ошибка комнаты: {}", exc.getMessage());
        return new ErrorResponse(exc.getCode(), exc.getMessage());
    }

    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleGenericException(Exception exc) {
        log.error("Необработанная ошибка: ", exc);
        return new ErrorResponse("INTERNAL_ERROR", "Произошла внутренняя ошибка сервера");
    }
}
