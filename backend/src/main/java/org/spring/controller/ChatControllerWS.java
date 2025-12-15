package org.spring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.dto.Room;
import org.spring.dto.Message;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ChatControllerWS {

    private static final Logger log = LoggerFactory.getLogger(ChatControllerWS.class);
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, Room> roomStore = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoomMap = new ConcurrentHashMap<>(); // sessionId -> roomId
    private static final Pattern roomTopicPattern = Pattern.compile("/topic/room/(.+)");

    public ChatControllerWS(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    //@SendToUser("/queue/whoami") означает, что ответ придёт только конкретному пользователю, у которого открыт этот WebSocket-сессия.
    @MessageMapping("/rooms/whoami")
    @SendToUser("/queue/whoami")
    public String sendSessionId(StompHeaderAccessor headerAccessor) {
        return headerAccessor.getSessionId(); // возвращаем уникальный ID соединения
    }

    @MessageMapping("/rooms/add")
    @SendToUser("/queue/rooms/created")
    public Room addRoom(@Payload String roomName, StompHeaderAccessor headerAccessor) {
        Room newRoom = new Room(UUID.randomUUID().toString(), roomName);
        roomStore.put(newRoom.getId(), newRoom);

        // Добавляем создателя сразу в комнату
        // String sessionId = headerAccessor.getSessionId();
        // if (sessionId != null) {
        //     newRoom.addParticipant(sessionId);
        //     sessionToRoomMap.put(sessionId, newRoom.getId());
        // }

        broadcastUpdatedRooms();
        return newRoom;
    }

    @MessageMapping("/room/{roomId}/start")
    public void startGame(@DestinationVariable String roomId) {
        Room room = roomStore.get(roomId);
        if (room == null) return;
        if (room.getParticipantsCount() < 2) return;

        room.setTurnIndex(0); // начинаем с первого игрока

        Map<String, Object> turnState = Map.of(
            "currentTurn", room.getCurrentTurnPlayer(),
            "participants", room.getParticipants()
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/turn", turnState);
    }

    @MessageMapping("/room/{roomId}/endTurn")
    public void endTurn(@DestinationVariable String roomId, StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        Room room = roomStore.get(roomId);
        if (room == null) return;

        if (!sessionId.equals(room.getCurrentTurnPlayer())) return;
        log.info("End turn requested by: {}", sessionId);
        log.info("Current turn before: {}", room.getCurrentTurnPlayer());
        room.nextTurn();
        log.info("Current turn after: {}", room.getCurrentTurnPlayer());


        Map<String, Object> turnState = Map.of(
            "currentTurn", room.getCurrentTurnPlayer(),
            "participants", room.getParticipants()
        );
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/turn", turnState);
    }

    @MessageMapping("/rooms/get/{roomId}")
    @SendTo("/topic/rooms/{roomId}")
    public Room getRoom(@DestinationVariable String roomId) {
        return roomStore.get(roomId);
    }

    @MessageMapping("/room/{roomId}")
    public void handleGameEvent(@DestinationVariable String roomId, @Payload Map<String, Object> event) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
    }

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Message sendGlobal(Message message) {
        return message;
    }

    @MessageMapping("/chat/leave")
    public void leaveRoom(StompHeaderAccessor headerAccessor) {
        handleLeave(headerAccessor.getSessionId());
    }

    @MessageMapping("/rooms/delete")
    public void deleteRoom(@Payload String roomId) {
        Room removedRoom = roomStore.remove(roomId);
        if (removedRoom != null) broadcastUpdatedRooms();
    }

    @MessageMapping("/rooms/list")
    @SendTo("/topic/rooms")
    public Collection<Room> listRooms() {
        return roomStore.values();
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        if (destination == null) return;

        Matcher matcher = roomTopicPattern.matcher(destination);
        if (!matcher.matches()) return;

        String roomId = matcher.group(1);
        String sessionId = headerAccessor.getSessionId();
        Room room = roomStore.get(roomId);
        if (room == null || sessionId == null) return;

        if (room.getParticipants().contains(sessionId)) return;
        if (room.isFull()) return;

        room.addParticipant(sessionId);
        sessionToRoomMap.put(sessionId, roomId);
        broadcastUpdatedRooms();
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        handleLeave(event.getSessionId());
    }

    private void broadcastUpdatedRooms() {
        messagingTemplate.convertAndSend("/topic/rooms", roomStore.values());
    }

    private void handleLeave(String sessionId) {
        if (sessionId == null) return;
        String roomId = sessionToRoomMap.remove(sessionId);
        if (roomId != null) {
            Room room = roomStore.get(roomId);
            if (room != null) {
                room.removeParticipant(sessionId);
                if (room.getParticipantsCount() == 0) roomStore.remove(roomId);
                broadcastUpdatedRooms();
            }
        }
    }
}
