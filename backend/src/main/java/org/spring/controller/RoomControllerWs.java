package org.spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.RoomRequest;
import org.spring.dto.Room;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoomControllerWs {

    private final SimpMessagingTemplate template;
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

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
        template.convertAndSend("/topic/rooms", rooms.values());
        log.info("rooms updated {} ", rooms.values());
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

    /**
     * Получить комнату по ID или отправить ошибку
     */
    private Room getRoomOrSendError(String roomId, Principal principal) {
        if (roomId == null) {
            sendError(principal, "Room ID пустой");
            return null;
        }
        Room room = rooms.get(roomId);
        if (room == null) {
            sendError(principal, "Комнаты с таким ID не существует");
        }
        return room;
    }

    /**
     * Проверка есть ли пользователь в других комнатах
     */
    private boolean isUserInAnyRoom(String username) {
        return rooms.values().stream()
                .anyMatch(r -> r.getPlayers().contains(username));
    }

    /**
     * Изменение флага заполненности комнаты
     */
    private void updateRoomState(Room room) {
        room.setIsFull(room.getPlayers().size() >= 2);
    }

    /** Генерация уникального короткого ID комнаты */
    private String generateRoomId() {
        // короткий уникальный id
        return UUID.randomUUID().toString().substring(0, 8);
    }

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
    public void createRoom(Principal principal) {

        log.info("/app/room.create зашел");

        if (!requireAuth(principal)) {
            return;
        }

        String creatorName = principal.getName();

        if (isUserInAnyRoom(creatorName)) {
            sendError(principal, "Вы уже находитесь в комнате");
            return;
        }

        Room room = new Room (generateRoomId(), "Комната " + creatorName, false, creatorName);
        room.getPlayers().add(creatorName);
        rooms.put(room.getId(), room);

        template.convertAndSendToUser(creatorName,"/queue/room.created", room);
        broadcastUpdatedRooms();

        log.info("/app/room.create отработал");
    }

    /**
     * Присоединение текущего игрока к комнате.
     * @param  req {@link RoomRequest} {roomId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.join")
    public void joinRoom(@Payload RoomRequest req, Principal principal) {

        log.info("/app/room.join зашел");

        if (!requireAuth(principal)) {
            return;
        }

        String username = principal.getName();

        Room room = getRoomOrSendError(req.roomId(), principal);
        if (room == null) {
            return;
        }

        if (room.getPlayers().size() >= 2) {
            sendError(principal, "Комната заполнена");
            log.error("Комната заполнена");
            return;
        }

        if (room.getPlayers().contains(principal.getName())) {
            sendError(principal, "Вы уже находитесь в этой комнате");
            log.error("Пользователь уже находится в комнате {}", room.getId());
            return;
        }
        if (isUserInAnyRoom(username)) {
            sendError(principal, "Вы уже находитесь в другой комнате");
            log.error("Пользователь уже находится в другой комнате");
            return;
        }

        room.getPlayers().add(username);

        updateRoomState(room);

        broadcastUpdatedRooms();

        log.info("/app/room.join отработал");
    }

    /**
     * Запрос на маршрут: /app/room.leave
     * @param  req {@link RoomRequest} {roomId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.leave")
    public void leaveRoom(@Payload RoomRequest req, Principal principal) {
        log.info("/app/room.leave зашел");

        if (!requireAuth(principal)) {
            return;
        }

        String username = principal.getName();

        Room room = getRoomOrSendError(req.roomId(), principal);
        if (room == null) {
            return;
        }

        if (!room.getPlayers().remove(username)) {
            sendError(principal, "Вы не находитесь в этой комнате");
            log.error("Пользователь {} не находитесь в комнате {}", principal.getName(), room.getId());
            return;
        }

        if (room.getPlayers().isEmpty()) {
            rooms.remove(room.getId());
        } else {
            updateRoomState(room);
        }

        broadcastUpdatedRooms();

        log.info("/app/room.leave отработал");
    }

    /**
     * Запрос на маршрут: /app/room.delete
     * @param  req {@link RoomRequest} {roomId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.delete")
    public void deleteRoom(@Payload RoomRequest req, Principal principal) {

        log.info("/app/room.delete зашел");

        if (!requireAuth(principal)) {
            return;
        }

        Room room = getRoomOrSendError(req.roomId(), principal);
        if (room == null) {
            return;
        }

        if (!room.getCreatorName().equals(principal.getName())) {
            sendError(principal, "Вы не можете удалить комнату");
            log.error("Пользователь {} не является создателем комнаты {} , которую пытается удалить", principal.getName(), room.getId());
            return;
        }

        rooms.remove(room.getId());

        broadcastUpdatedRooms();

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
    public Collection<Room> listRooms() {
        log.info("/app/room.list зашел");
        broadcastUpdatedRooms();
        log.info("/app/room.list отработал");
        return rooms.values();
    }

    public void leaveAllRooms(String username) {

        rooms.values().forEach(room -> room.getPlayers().remove(username));

        rooms.entrySet().removeIf(e -> e.getValue().getPlayers().isEmpty());

        rooms.values().forEach(this::updateRoomState);

        broadcastUpdatedRooms();

        log.info("User {} removed from all rooms", username);
    }
}
