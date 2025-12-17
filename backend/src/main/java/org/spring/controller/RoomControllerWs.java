package org.spring.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.spring.dto.GameState;
import org.spring.dto.JoinRequest;
import org.spring.dto.Room;
import org.spring.service.impl.GameService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@Slf4j
@RequiredArgsConstructor
public class RoomControllerWs {

    private final SimpMessagingTemplate template;
    private final GameService gameService;
    private final ObjectMapper objectMapper;

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
        template.convertAndSendToUser(principal.getName(), "/errors", error);
    }

    /**
     * Создание новой игровой комнаты.
     * Запрос на маршрут: /app/room.create
     * Ответ отправляется на маршрут "/user/room.created"
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
    public void createRoom(Principal principal) throws JsonProcessingException {
        if (principal == null) {
            log.error("Пользователь не авторизован");
            return;
        }
        String username = principal.getName();
        GameState gs = gameService.createRoom(username);
        Room room = new Room(gs.getRoomId(), "Комната " + principal.getName(), false);
        rooms.put(room.getId(), room);
        // send STATE_UPDATE только создателю: используем user-queue
        template.convertAndSendToUser(username, "/room.created", room.toString());
        broadcastUpdatedRooms();
        log.info("/room.create отправил пользователю {} по пути /room.created объект Room {}", username, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(room));
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

        if (principal == null) {
            log.error("Пользователь не авторизован");
            return;
        }
        if (req == null || req.roomId() == null) {
            sendErrorToUser(principal, "roomId required");
            return;
        }
        Optional<GameState> opt = gameService.findRoom(req.roomId());
        if (opt.isEmpty()) {
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
            gs = gameService.findRoom(req.roomId()).isPresent() ? gameService.findRoom(req.roomId()).get() : null;
            assert gs != null;
            Room room = rooms.get(gs.getRoomId());
            if (room.isFull()){
                return;
            }
            room.setIsFull(true);
            broadcastUpdatedRooms();
        } catch (IllegalStateException ex) {
            sendErrorToUser(principal, ex.getMessage());
        }
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
        broadcastUpdatedRooms();
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
        Room room = rooms.get(req.roomId());
        room.getPlayers().remove(principal.getName());
        broadcastUpdatedRooms();
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
        Room removed = rooms.remove(req.roomId());
        if (removed != null){
            broadcastUpdatedRooms();
        }
    }
}
