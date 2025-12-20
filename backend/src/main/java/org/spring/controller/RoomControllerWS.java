package org.spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.RoomRequest;
import org.spring.dto.Room;
import org.spring.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Collection;

@Controller
@RequiredArgsConstructor
@Slf4j
public class RoomControllerWS {

    private final RoomService roomService;
    private final SimpMessagingTemplate template;

    /* ========================= API ========================= */

    /**
     * Создание новой игровой комнаты.
     * Запрос на маршрут: /app/room.create
     * Ответ отправляется на маршрут "/user/queue/room.created"
     * {
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * "creatorName": user
     * }
     *
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.create")
    public void createRoom (Principal principal) {

        log.info("/app/room.create зашел");

        if (!requireAuth(principal)) {
            log.error("Пользователь не авторизован");
            return;
        }

        try {
            Room room = roomService.createRoom(principal.getName());
            template.convertAndSendToUser(principal.getName(), "/queue/room.created", room);
            broadcastUpdatedRooms();
        } catch (Exception e) {
            sendError(principal, e.getMessage());
        }

        log.info("/app/room.create отработал");
    }

    /**
     * Присоединение текущего игрока к комнате.
     * @param  req {@link RoomRequest} {gameId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.join")
    public void joinRoom(@Payload RoomRequest req, Principal principal) {

        log.info("/app/room.join зашел");

        if (!requireAuth(principal)) {
            log.error("Пользователь не авторизован");
            return;
        }

        try {
            roomService.joinRoom(req.roomId(), principal.getName());
            broadcastUpdatedRooms();
        } catch (Exception e) {
            sendError(principal, e.getMessage());
        }

        log.info("/app/room.join отработал");
    }

    /**
     * Запрос на маршрут: /app/room.leave
     * @param  req {@link RoomRequest} {gameId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.leave")
    public void leaveRoom(@Payload RoomRequest req, Principal principal) {
        log.info("/app/room.leave зашел");

        if (!requireAuth(principal)) {
            log.error("Пользователь не авторизован");
            return;
        }

        try {
            roomService.leaveRoom(req.roomId(), principal.getName());
            broadcastUpdatedRooms();
        } catch (Exception e) {
            sendError(principal, e.getMessage());
        }

        log.info("/app/room.leave отработал");
    }

    /**
     * Запрос на маршрут: /app/room.delete
     * @param  req {@link RoomRequest} {gameId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.delete")
    public void deleteRoom(@Payload RoomRequest req, Principal principal) {

        log.info("/app/room.delete зашел");

        if (!requireAuth(principal)) {
            log.error("Пользователь не авторизован");
            return;
        }

        try {
            roomService.deleteRoom(req.roomId(), principal.getName());
            broadcastUpdatedRooms();
        } catch (Exception e) {
            sendError(principal, e.getMessage());
        }

        log.info("/app/room.delete отработал");
    }

    /**
     * Запрос на маршрут: /app/room.list
     * Ответ отправляется на маршрут "/topic/rooms"
     * [{
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * "creatorName": user
     * }]
     */
    @MessageMapping("/room.list")
    public void listRooms() {
        log.info("/app/room.list зашел");
        broadcastUpdatedRooms();
        log.info("/app/room.list отработал");
    }

    /* ========================= UTIL ========================= */

    /**
     * Обновить текущие комнаты
     * Ответ в виде JSON отправляется на маршрут "/topic/rooms"
     * [{
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * "creatorName": user
     * }]
     */
    private void broadcastUpdatedRooms() {
        template.convertAndSend("/topic/rooms", roomService.listRooms());
    }

    /**
     * Отправка ошибки пользователю в виде СТРОКИ на маршрут "/user/queue/errors"
     */
    private void sendError(Principal principal, String message) {
        if (principal != null) {
            template.convertAndSendToUser(principal.getName(), "/queue/errors", message);
        }
    }

    /**
     * Проверка авторизации
     */
    private boolean requireAuth(Principal principal) {
        return principal != null;

    }
}
