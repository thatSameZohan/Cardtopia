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

    private void broadcastUpdatedRooms() {
        template.convertAndSend("/topic/rooms", rooms.values());
        log.info("rooms updated {} ", rooms.values());
    }

    private void sendErrorToUser(Principal principal, String error) {
        if (principal == null){
            return;
        }
        template.convertAndSendToUser(principal.getName(), "/queue/errors", error);
    }

    /**
     * Создание новой игровой комнаты.
     * <p>
     * STOMP маршрут: /app/room.create
     * Заголовок: Authorization: Bearer +accessToken;
     * Ответ: отправляется по пути "/queue/room.created"
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
        template.convertAndSendToUser(username, "/queue/room.created", room.toString());
        broadcastUpdatedRooms();
        log.info("/room.create отправил пользователю {} по пути /queue/room.created объект Room {}", username, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(room));
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

    @MessageMapping("/room.list")
    public Collection<Room> listRooms() {
        return rooms.values();
    }

    @MessageMapping("/room.leave")
    public void leaveRoom(@Payload String roomId, Principal principal) {
        Room room = rooms.get(roomId);
        room.getPlayers().remove(principal.getName());
        broadcastUpdatedRooms();
    }

    @MessageMapping("/room.delete")
    public void deleteRoom(@Payload String roomId) {
        Room removed = rooms.remove(roomId);
        if (removed != null){
            broadcastUpdatedRooms();
        }
    }


}
