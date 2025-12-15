package org.spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.*;
import org.spring.service.impl.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Optional;

/**
 * Контроллер WebSocket для управления игровой логикой.
 * <p>
 * Обрабатывает сообщения STOMP от клиента на следующих маршрутах:
 * <ul>
 *     <li>/app/room.create — создание комнаты</li>
 *     <li>/app/room.join — присоединение к комнате</li>
 *     <li>/app/game.playCard — сыграть карту</li>
 *     <li>/app/game.buyCard — купить карту</li>
 *     <li>/app/game.attack — атака</li>
 *     <li>/app/game.endTurn — завершение хода</li>
 * </ul>
 * Все методы требуют заголовка Authorization с JWT access token.
 */
@Slf4j
@RequiredArgsConstructor
@Controller
class GameControllerWS {

    private final GameService gameService;
    private final SimpMessagingTemplate template;

    /**
     * Отправляет обновление состояния комнаты всем игрокам.
     *
     * @param gs текущее состояние игры {@link GameState}
     */
    private void broadcastState(GameState gs) {
        // отправить в topic для комнаты
        template.convertAndSend("/topic/room." + gs.getRoomId(), gs);
        // при необходимости можно отправлять персональные обновления каждому пользователю.
    }

    /**
     * Отправляет ошибку конкретному пользователю.
     *
     * @param principal авторизованный пользователь
     * @param error     текст ошибки
     */
    private void sendErrorToUser(Principal principal, String error) {
        if (principal == null) return;
        template.convertAndSendToUser(principal.getName(), "/queue/errors", error);
    }

    /**
     * Создание новой игровой комнаты.
     * <p>
     * STOMP маршрут: /app/room.create
     * Заголовок: Authorization: Bearer +accessToken;
     * Ответ: отправляется {@link GameState} только создателю через "/queue/room.{roomId}.state"
     *
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.create")
    public void createRoom(Principal principal) {
        if (principal == null){
            log.error("Пользователь не авторизован");
            return;
        };
        GameState gs = gameService.createRoom(principal.getName());
        // send STATE_UPDATE только создателю: используем user-queue
        template.convertAndSendToUser(principal.getName(), "/user/queue/room.created", gs);
//        template.convertAndSendToUser(principal.getName(), "/queue/room." + gs.getRoomId()+ ".state", gs);

    }
    /**
     * Присоединение текущего игрока к комнате.
     * <p>
     * STOMP маршрут: /app/room.join
     * Payload: {@link JoinRequest} {roomId (String)}
     *
     * @param req       запрос с ID комнаты
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.join")
    public void joinRoom(JoinRequest req, Principal principal) {
        if (principal == null) {
            return;
        }
        if (req == null || req.roomId() == null) {
            sendErrorToUser(principal, "roomId required");
            return;
        }
        Optional<GameState> opt = gameService.findRoom(req.roomId());
        if (!opt.isPresent()) {
            sendErrorToUser(principal, "Room not found");
            return;
        }
        GameState gs = opt.get();
        try {
            if (gs.getPlayers().size() >= 2) {
                sendErrorToUser(principal, "Room is full");
                return;
            }
            gameService.joinRoom(req.roomId(), principal.getName());
            // re-fetch state
            gs = gameService.findRoom(req.roomId()).get();
            broadcastState(gs);
        } catch (IllegalStateException ex) {
            sendErrorToUser(principal, ex.getMessage());
        }
    }

    /**
     * Сыграть карту игроком.
     * <p>
     * STOMP маршрут: /app/game.playCard
     * Payload: {@link PlayCardRequest} {roomId (String), cardId (String)}
     *
     * @param req       данные о карте и комнате
     * @param principal авторизованный игрок
     */
    @MessageMapping("/game.playCard")
    public void playCard(PlayCardRequest req, Principal principal) {
        if (principal == null) return;
        if (req == null || req.roomId() == null || req.cardId() == null) {
            sendErrorToUser(principal, "roomId and cardId required");
            return;
        }
        Optional<GameState> opt = gameService.findRoom(req.roomId());
        if (!opt.isPresent()) {
            sendErrorToUser(principal, "Room not found");
            return;
        }
        GameState gs = opt.get();
        synchronized (gs) {
            if (!principal.getName().equals(gs.getActivePlayerId())) {
                sendErrorToUser(principal, "Not your turn");
                return;
            }
            PlayerState p = gs.getPlayers().get(principal.getName());
            boolean has = p.getHand()
                    .stream()
                    .anyMatch(c -> c.getId().equals(req.cardId()));
            if (!has) {
                sendErrorToUser(principal, "Card not in hand");
                return;
            }
            try {
                gameService.playCard(gs, principal.getName(), req.cardId());
                broadcastState(gs);
            } catch (IllegalStateException ex) {
                sendErrorToUser(principal, ex.getMessage());
            }
        }
    }

    /**
     * Купить карту из рынка.
     * <p>
     * STOMP маршрут: /app/game.buyCard
     * Payload: {@link BuyCardRequest} {roomId (String), marketCardId (String)}
     *
     * @param req       данные о карте и комнате
     * @param principal авторизованный игрок
     */
    @MessageMapping("/game.buyCard")
    public void buyCard(BuyCardRequest req, Principal principal) {
        if (principal == null) return;
        if (req == null || req.roomId() == null || req.marketCardId() == null) {
            sendErrorToUser(principal, "roomId and marketCardId required");
            return;
        }
        Optional<GameState> opt = gameService.findRoom(req.roomId());
        if (!opt.isPresent()) {
            sendErrorToUser(principal, "Room not found");
            return;
        }
        GameState gs = opt.get();
        synchronized (gs) {
            if (!principal.getName().equals(gs.getActivePlayerId())) {
                sendErrorToUser(principal, "Not your turn");
                return;
            }
            PlayerState p = gs.getPlayers().get(principal.getName());

            Optional<Card> cardOpt = gs.getMarket()
                    .stream()
                    .filter(c -> c.getId().equals(req.marketCardId()))
                    .findFirst();

            if (!cardOpt.isPresent()) {
                sendErrorToUser(principal, "Card not in market");
                return;
            }
            Card card = cardOpt.get();

            if (p.getCurrentGold() < card.getCost()) {
                sendErrorToUser(principal, "Not enough gold");
                return;
            }
            try {
                gameService.buyCard(gs, principal.getName(), req.marketCardId());
                broadcastState(gs);
            } catch (IllegalStateException ex) {
                sendErrorToUser(principal, ex.getMessage());
            }
        }
    }

    /**
     * Атака игрока.
     * <p>
     * STOMP маршрут: /app/game.attack
     * Payload: {@link AttackRequest} {roomId (String)}
     *
     * @param req       данные о комнате
     * @param principal авторизованный игрок
     */
    @MessageMapping("/game.attack")
    public void attack(AttackRequest req, Principal principal) {
        if (principal == null) return;
        if (req == null || req.roomId() == null) {
            sendErrorToUser(principal, "roomId required");
            return;
        }
        Optional<GameState> opt = gameService.findRoom(req.roomId());
        if (!opt.isPresent()) {
            sendErrorToUser(principal, "Room not found");
            return;
        }
        GameState gs = opt.get();
        synchronized (gs) {
            if (!principal.getName().equals(gs.getActivePlayerId())) {
                sendErrorToUser(principal, "Not your turn");
                return;
            }
            try {
                gameService.attack(gs, principal.getName());
                broadcastState(gs);
            } catch (IllegalStateException ex) {
                sendErrorToUser(principal, ex.getMessage());
            }
        }
    }

    /**
     * Завершение хода игрока.
     * <p>
     * STOMP маршрут: /app/game.endTurn
     * Payload: {@link EndTurnRequest} {roomId (String)}
     *
     * @param req       данные о комнате
     * @param principal авторизованный игрок
     */
    @MessageMapping("/game.endTurn")
    public void endTurn(EndTurnRequest req, Principal principal) {
        if (principal == null) return;
        if (req == null || req.roomId() == null) {
            sendErrorToUser(principal, "roomId required");
            return;
        }
        Optional<GameState> opt = gameService.findRoom(req.roomId());
        if (!opt.isPresent()) {
            sendErrorToUser(principal, "Room not found");
            return;
        }
        GameState gs = opt.get();
        synchronized (gs) {
            if (!principal.getName().equals(gs.getActivePlayerId())) {
                sendErrorToUser(principal, "Not your turn");
                return;
            }
            try {
                gameService.endTurn(gs, principal.getName());
                broadcastState(gs);
            } catch (IllegalStateException ex) {
                sendErrorToUser(principal, ex.getMessage());
            }
        }
    }
}