package org.spring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.JoinRequest;
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

    /**
     * Ответ отправляется на маршрут "/topic/rooms"
     * [{
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * }]
     */
    private void broadcastUpdatedRooms() {
        template.convertAndSend("/topic/rooms", rooms.values());
        log.info("rooms updated {} ", rooms.values());
    }

    private void sendErrorToUser(Principal principal, String error) {
        if (principal == null){
            return;
        }
        template.convertAndSendToUser(principal.getName(),"/queue/errors/", error);
    }

    /** Генерация уникального короткого ID комнаты */
    private String generateRoomId() {
        // короткий уникальный id
        return UUID.randomUUID().toString().substring(0, 8);
    }

    /**
     * Создание новой игровой комнаты.
     * Запрос на маршрут: /app/room.create
     * Ответ отправляется на маршрут "/user/queue/room.created"  и
     * {
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * }
     *
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.create")
    public void createRoom(Principal principal) {
        log.info("/app/room.create зашел");
        if (principal == null) {
            log.error("Пользователь не авторизован");
            return;
        }
        String creatorName = principal.getName();
        Room room = new Room(generateRoomId(), "Комната " + creatorName, false);
        room.getPlayers().add(creatorName);
        rooms.put(room.getId(), room);
        template.convertAndSendToUser(principal.getName(),"/queue/room.created", room);
        broadcastUpdatedRooms();
        log.info("/app/room.create отработал");
    }

    /**
     * Присоединение текущего игрока к комнате.
     * <p>
     * Запрос на маршрут: /app/room.join
     * Ответ отправляется на маршрут "/topic/rooms"
     * [{
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * }]
     * @param  req {@link JoinRequest} {roomId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.join")
    public void joinRoom(@Payload JoinRequest req, Principal principal) {

        log.info("/app/room.join зашел");

        if (principal == null) {
            log.error("Пользователь не авторизован");
            return;
        }

        if (req == null || req.roomId() == null) {
            sendErrorToUser(principal, "roomId required");
            log.error("roomId required");
            return;
        }

        Room room = rooms.get(req.roomId());

        if (room == null) {
            sendErrorToUser(principal, "Room not found");
            log.error("Room not found");
            return;
        }

        if (room.getPlayers().size() >= 2) {
            sendErrorToUser(principal, "Room is full");
            log.error("Room is full");
            return;
        }

        if (room.getPlayers().contains(principal.getName())) {
            sendErrorToUser(principal, "You is already in the room");
            log.error("Player is already in room");
            return;
        }

        room.getPlayers().add(principal.getName());

        room.setIsFull(true);

        broadcastUpdatedRooms();

        log.info("/app/room.join отработал");
    }

    /**
     * Запрос на маршрут: /app/room.list
     * Ответ отправляется на маршрут "/topic/rooms"
     * [{
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * }]
     */
    @MessageMapping("/room.list")
    public Collection<Room> listRooms() {
        log.info("/app/room.list зашел");
        broadcastUpdatedRooms();
        log.info("/app/room.list отработал");
        return rooms.values();
    }

    /**
     * Запрос на маршрут: /app/room.leave
     * Ответ отправляется на маршрут "/topic/rooms"
     * [{
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * }]
     * @param  req {@link JoinRequest} {roomId (String)}
     * @param principal авторизованный пользователь
     */
    @MessageMapping("/room.leave")
    public void leaveRoom(@Payload JoinRequest req, Principal principal) {
        log.info("/app/room.leave зашел");
        Room room = rooms.get(req.roomId());
        room.getPlayers().remove(principal.getName());

        if (room.getPlayers().isEmpty()) {
            rooms.remove(room.getId());
        }

        broadcastUpdatedRooms();
        log.info("/app/room.leave отработал");
    }

    /**
     * Запрос на маршрут: /app/room.delete
     * Ответ отправляется на маршрут "/topic/rooms"
     * [{
     * "id" : "1ead51a1",
     * "name" "Комната +username"
     * "players" : [ "user" ]
     * "isFull" : false
     * }]
     * @param  req {@link JoinRequest} {roomId (String)}
     */
    @MessageMapping("/room.delete")
    public void deleteRoom(@Payload JoinRequest req) {
        log.info("/app/room.delete зашел");
        Room removed = rooms.remove(req.roomId());
        if (removed != null){
            broadcastUpdatedRooms();
        }
        log.info("/app/room.delete отработал");
    }
}
