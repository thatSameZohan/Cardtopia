package org.spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.*;
import org.spring.enums.GameStatus;
import org.spring.mapper.GameViewMapper;
import org.spring.service.GameService;
import org.spring.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class GameControllerWS {

    private final GameService gameService;
    private final RoomService roomService;
    private final SimpMessagingTemplate template;
    private final GameViewMapper gameViewMapper;
    private final Map<String, Object> gameLocks = new ConcurrentHashMap<>();

    /* ========================= API ========================= */

    @MessageMapping("/game.start")
    public void startGame (@Payload StartGameRequest req, Principal principal) {

        if (!requireAuth(principal)) {
            return;
        }

        Optional<Room> opt = roomService.findRoom(req.roomId());

        if (opt.isEmpty()) {
            sendError(principal, "Комната не найдена");
            return;
        }

        Room room = opt.get();

        try {

            // Создаём игру с новым gameId
            GameState gs = roomService.startGame(req.roomId(), principal.getName());
            String gameId = gs.getId();

            // Отправляем всем в комнате персонализированное состояние игры
            broadcastState(gs);

            // Отправляем инициатору игры реальные ID
            template.convertAndSendToUser(principal.getName(), "/queue/game.started", Map.of(
                    "roomId", room.getId(),
                    "gameId", gameId
            ));

            // Сигнал для всех участников комнаты, что игра стартовала
            template.convertAndSend("/topic/game.start." + room.getId(), Map.of(
                    "gameId", gameId,
                    "message", "start"
            ));

        } catch (Exception e) {
            sendError(principal, e.getMessage());
        }
    }

    @MessageMapping("/game.init")
    public void initGame(@Payload InitGameRequest req, Principal principal) {

        if (principal == null) return;

        validateAndGetGame(req.gameId(), principal)
                .ifPresent(gs -> {
                    broadcastState(gs);
                });
    }

    @MessageMapping("/game.playCard")
    public void playCard(@Payload PlayCardRequest req, Principal principal) {

        if (req == null) {
            sendError(principal, "Вы не указали какой картой хотите сыграть");
            log.error("Не был получен {gameId, cardId");
            return;
        }

        validateAndGetGame(req.gameId(), principal)
                .ifPresent(gs -> {
                    synchronized (lockFor(gs.getId())) {
                        if (gs.getStatus().equals(GameStatus.FINISHED)){
                            sendError(principal, "Игра окончена");
                            return;
                        }
                        if (!gs.isPlayersTurn(principal.getName())) {
                            sendError(principal, "Не ваш ход");
                            log.error("У игрока {} нет хода", principal.getName());
                            return;
                        }
                        gameService.playCard(gs, principal.getName(), req.cardId());
                        broadcastState(gs);
                    }
                });
    }

    @MessageMapping("/game.buyCard")
    public void buyCard(BuyCardRequest req, Principal principal) {

        if (req == null) {
            sendError(principal, "Вы не указали какую карту хотите купить");
            log.error("не был получен {gameId, marketCardId}");
            return;
        }

        validateAndGetGame(req.gameId(), principal)
                .ifPresent(gs -> {
                    synchronized (lockFor(gs.getId())) {
                        if (gs.getStatus().equals(GameStatus.FINISHED)){
                            sendError(principal, "Игра окончена");
                            return;
                        }
                        if (!gs.isPlayersTurn(principal.getName())) {
                            sendError(principal, "Не ваш ход");
                            log.error("У игрока {} нет хода", principal.getName());
                            return;
                        }
                        gameService.buyCard(gs, principal.getName(), req.marketCardId());
                        broadcastState(gs);
                    }
                });
    }


    @MessageMapping("/game.attack")
    public void attack(AttackRequest req, Principal principal) {

        if (req == null) {
            sendError(principal, "Утерян ID игры");
            log.error("Не был получен {gameId}");
            return;
        }

        validateAndGetGame(req.gameId(), principal)
                .ifPresent(gs -> {
                    synchronized (lockFor(gs.getId())) {
                        if (gs.getStatus().equals(GameStatus.FINISHED)){
                            sendError(principal, "Игра окончена");
                            return;
                        }
                        if (!gs.isPlayersTurn(principal.getName())) {
                            sendError(principal, "Не ваш ход");
                            log.error("У игрока {} нет хода", principal.getName());
                            return;
                        }
                        gameService.attack(gs, gs.getActivePlayerId());
                        broadcastState(gs);
                    }
                });
    }

    @MessageMapping("/game.endTurn")
    public void endTurn(EndTurnRequest req, Principal principal) {

        if (req == null) {
            sendError(principal, "Утерян ID игры");
            log.error("Не был получен {gameId}");
            return;
        }

        validateAndGetGame(req.gameId(), principal)
                .ifPresent(gs -> {
                    synchronized (lockFor(gs.getId())) {
                        if (gs.getStatus().equals(GameStatus.FINISHED)){
                            sendError(principal, "Игра окончена");
                            return;
                        }
                        if (!gs.isPlayersTurn(principal.getName())) {
                            sendError(principal, "Не ваш ход");
                            log.error("У игрока {} нет хода", principal.getName());
                            return;
                        }
                        gameService.endTurn(gs, gs.getActivePlayerId());
                        broadcastState(gs);
                    }
                });

    }

    /* ========================= UTIL ========================= */

    private Object lockFor(String gameId) {
        return gameLocks.computeIfAbsent(gameId, id -> new Object());
    }

    private void broadcastState(GameState gs) {

        gs.getPlayers().keySet().forEach(username -> {

            GameView view = gameViewMapper.toView(gs, username);

            template.convertAndSendToUser(
                    username,
                    "/queue/game." + gs.getId(),
                    view
            );
        });
    }

    /**
     * Отправка ошибки пользователю в виде СТРОКИ на маршрут "/user/queue/errors"
     */
    private void sendError(Principal principal, String message) {
        if (principal == null){
            return;
        }
        template.convertAndSendToUser(principal.getName(),"/queue/errors", message);
    }

    /**
     * Проверка авторизации
     */
    private boolean requireAuth(Principal principal) {
        if (principal == null) {
            log.error("Пользователь не авторизован");
            return false;
        }
        return true;
    }

    private Optional<GameState> validateAndGetGame(String gameId, Principal principal) {

        if (!requireAuth(principal)) {
            return Optional.empty();
        }

        Optional<GameState> opt = gameService.findGame(gameId);

        if (opt.isEmpty()) {
            sendError(principal, "Игра не найдена");
            return Optional.empty();
        }
        return opt;
    }
}
