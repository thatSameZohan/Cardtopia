package org.spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.ErrorResponse;
import org.spring.dto.RoomRequest;
import org.spring.dto.Room;
import org.spring.exc.RoomCommonException;
import org.spring.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RoomControllerWS {

    private final RoomService roomService;
    private final SimpMessagingTemplate template;

    /* ========================= WebSocket API ========================= */

    /**
     * Создание новой игровой комнаты.
     * Запрос на маршрут: /app/room.create
     * Ответ отправляется на маршрут "/user/queue/room.created"
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.create")
    public void createRoom (Principal principal) {

        requireAuth(principal);
        Room room = roomService.createRoom(principal.getName());
        template.convertAndSendToUser(principal.getName(), "/queue/room.created", room);
        broadcastUpdatedRooms();
        log.info("/app/room.create отработал");
    }

    /**
     * Присоединение текущего игрока к комнате.
     * Запрос на маршрут: /app/room.join
     * @param  req {@link RoomRequest} {gameId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.join")
    public void joinRoom(@Payload RoomRequest req, Principal principal) {

        requireAuth(principal);
        roomService.joinRoom(req.roomId(), principal.getName());
        broadcastUpdatedRooms();
        log.info("/app/room.join отработал");
    }

    /**
     * Запрос на маршрут: /app/room.leave
     * @param  req {@link RoomRequest} {gameId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.leave")
    public void leaveRoom(@Payload RoomRequest req, Principal principal) {

        requireAuth(principal);
        roomService.leaveRoom(req.roomId(), principal.getName());
        broadcastUpdatedRooms();
        log.info("/app/room.leave отработал");
    }

    /**
     * Запрос на маршрут: /app/room.delete
     * @param  req {@link RoomRequest} {gameId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.delete")
    public void deleteRoom(@Payload RoomRequest req, Principal principal) {

        requireAuth(principal);
        roomService.deleteRoom(req.roomId(), principal.getName());
        broadcastUpdatedRooms();
        log.info("/app/room.delete отработал");
    }

    /**
     * Получить список комнат
     * Запрос на маршрут: /app/room.list
     * Ответ отправляется на маршрут "/topic/rooms"
     */
    @MessageMapping("/room.list")
    public void listRooms() {
        broadcastUpdatedRooms();
        log.info("/app/room.list отработал");
    }

    /* ========================= UTIL ========================= */

    /**
     * Обновить текущие комнаты
     * Ответ в виде JSON отправляется на маршрут "/topic/rooms"
     */
    private void broadcastUpdatedRooms() {
        template.convertAndSend("/topic/rooms", roomService.listRooms());
    }

    /**
     * Проверка авторизации
     */
    private void requireAuth(Principal principal) {
        if (principal == null) {
            throw new RoomCommonException("UNAUTHORIZED", "Пользователь не авторизован");
        }
    }

    /* ========================= Exception Handling ========================= */

    /**
     * Централизованная обработка RoomCommonException
     * Ошибки отправляются пользователю на /user/queue/errors
     */
    @MessageExceptionHandler(RoomCommonException.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleRoomException (RoomCommonException exc) {
        log.error("Ошибка комнаты: {}", exc.getMessage());
        return new ErrorResponse(exc.getCode(), exc.getMessage());
    }

    /**
     * Обработка любых необработанных ошибок (например, NullPointerException)
     */
    @MessageExceptionHandler(Exception.class)
    @SendToUser("/queue/errors")
    public ErrorResponse handleGenericException(Exception exc) {
        log.error("Необработанная ошибка: ", exc);
        return new ErrorResponse("INTERNAL_ERROR", "Произошла внутренняя ошибка сервера");
    }
}
