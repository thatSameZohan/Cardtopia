package org.spring.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.spring.model.Message;
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
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    // In-memory хранилище комнат
    private final Map<String, Room> roomStore = new ConcurrentHashMap<>();
    private final Map<String, String> sessionToRoomMap = new ConcurrentHashMap<>(); // sessionId -> roomId
    private static final Pattern roomTopicPattern = Pattern.compile("/topic/room/(.+)");
 
    public ChatController(SimpMessagingTemplate messagingTemplate, ObjectMapper objectMapper) {
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }

    // DTO комнаты
    public static class Room {
        private String id;
        private String name;
        private final Set<String> participants = ConcurrentHashMap.newKeySet();

        public Room() {} // для сериализации
        public Room(String id, String name) { this.id = id; this.name = name; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        @JsonProperty("participantsCount")
        public int getParticipantsCount() { return participants.size(); }
        public boolean isFull() { return participants.size() >= 2; }
        public void addParticipant(String sessionId) { participants.add(sessionId); }
        public void removeParticipant(String sessionId) { participants.remove(sessionId); }

 public Set<String> getParticipants() {
        return participants;
    }

        @Override
        public String toString() {
            return "Room{" + "id='" + id + '\'' + ", name='" + name + "', participants=" + getParticipantsCount() + '}';
        }
    }

// Запрос состояния конкретной комнаты.
// Клиент отправляет запрос на /app/rooms/get/{roomId},
// сервер возвращает объект Room и публикует его в /topic/rooms/{roomId}.
// Используется для получения информации о комнате при входе или обновления UI.
// Это нужная залупа
@MessageMapping("/rooms/get/{roomId}")
@SendTo("/topic/rooms/{roomId}")
public Room getRoom(@DestinationVariable String roomId) {
    return roomStore.get(roomId);
}
@MessageMapping("/room/{roomId}")
public void handleGameEvent(@DestinationVariable String roomId, @Payload Map<String, Object> event) {
    messagingTemplate.convertAndSend("/topic/room/" + roomId, event);
}


    // Отправка сообщения в конкретную комнату
    // @MessageMapping("/chat/{roomId}")
    // public void sendMessage(@DestinationVariable String roomId, Message message) {
    //     messagingTemplate.convertAndSend("/topic/room/" + roomId, message);
    // }
// моя правка для общего чата 
@MessageMapping("/chat")
@SendTo("/topic/chat")
public Message sendGlobal(Message message) {
    return message;
}
    @MessageMapping("/chat/leave")
    public void leaveRoom(StompHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        handleLeave(sessionId);
    }

    // Создание комнаты
@MessageMapping("/rooms/add")
@SendToUser("/queue/rooms/created")
public Room addRoom(@Payload String roomName, StompHeaderAccessor headerAccessor) {
    Room newRoom = new Room(UUID.randomUUID().toString(), roomName);
    roomStore.put(newRoom.getId(), newRoom);
    //автоматически добавляем создателя в комнату
    // я хуи знаю как но нужно сделать так что бы создатель номнаты сразу заходил в нее
    //без этого работает но не уверен что верно так как регистрируюсь я при входе
    // String sessionId = headerAccessor.getSessionId();
    // if (sessionId != null) {
    //  newRoom.addParticipant(sessionId);
    //     sessionToRoomMap.put(sessionId, newRoom.getId());
    // }
     broadcastUpdatedRooms(); // уведомляем всех о новой комнате
    return newRoom; // комната возвращается создателю
}

    // Удаление комнаты
    @MessageMapping("/rooms/delete")
    public void deleteRoom(@Payload String roomId) {
        log.info("[BE] Received request to delete room with ID: {}", roomId);
        Room removedRoom = roomStore.remove(roomId);
        if (removedRoom != null) {
            log.info("Room removed: {}. Total rooms now: {}", removedRoom, roomStore.size());
            broadcastUpdatedRooms();
        } else {
            log.warn("Attempted to delete a non-existent room with ID: {}", roomId);
        }
    }

    // При подключении нового пользователя — отдаем текущие комнаты
    @MessageMapping("/rooms/list")
    @SendTo("/topic/rooms")
    public Collection<Room> listRooms() {
        log.info("Request for room list received. Sending {} rooms.", roomStore.size());
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

    if (room == null || sessionId == null) {
        log.warn("Subscribe failed: room or session is null. roomId={}, sessionId={}", roomId, sessionId);
        return;
    }

    // 🔥 Новая проверка — участник не должен быть добавлен дважды
    if (room.getParticipants().contains(sessionId)) {
        log.info("Session {} already in room {}, skip adding.", sessionId, roomId);
        return;
    }

    // Проверка на заполненность комнаты
    if (room.isFull()) {
        log.warn("Room {} is full. Session {} cannot join.", roomId, sessionId);
        return;
    }

    // Добавляем игрока
    room.addParticipant(sessionId);
    sessionToRoomMap.put(sessionId, roomId);

    log.info("Session {} subscribed to room {}. Participants: {}", 
            sessionId, roomId, room.getParticipantsCount());

    broadcastUpdatedRooms();
}

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        handleLeave(sessionId);
    }


    private void broadcastUpdatedRooms() {
        Collection<Room> rooms = roomStore.values();
        try {
            String roomsAsJson = objectMapper.writeValueAsString(rooms);
            log.info(">>> [BACKEND] Broadcasting rooms to /topic/rooms: {}", roomsAsJson);
        } catch (JsonProcessingException e) {
            log.error(">>> [BACKEND] Error serializing rooms to JSON", e);
        }
        messagingTemplate.convertAndSend("/topic/rooms", rooms);
    }

    private void handleLeave(String sessionId) {
    if (sessionId == null) return;

    String roomId = sessionToRoomMap.remove(sessionId);
    if (roomId != null) {
        Room room = roomStore.get(roomId);
        if (room != null) {
            room.removeParticipant(sessionId);
            // если комната пуста, удаляем её
            if (room.getParticipantsCount() == 0) {
                roomStore.remove(roomId);
                log.info("Room {} deleted because it became empty", roomId);
            }
            broadcastUpdatedRooms();
        }
    }
}
}
