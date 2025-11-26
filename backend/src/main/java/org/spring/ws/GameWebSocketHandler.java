package org.spring.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object uidObj = session.getAttributes().get("userId");
        if (uidObj == null) { session.close(CloseStatus.NOT_ACCEPTABLE); return; }
        Long userId = (Long) uidObj;
        sessions.put(userId, session);
// send welcome
        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of("type","welcome","userId",userId))));
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> msg = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) msg.get("type");
        Object uidObj = session.getAttributes().get("userId");
        Long userId = uidObj==null?null:(Long)uidObj;
        if ("ping".equals(type)) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of("type","pong"))));
            return;
        }
// handle join_match / play_card / etc - server authoritative
// For demo, echo
        session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of("type","echo","payload",msg))));
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Object uidObj = session.getAttributes().get("userId");
        if (uidObj!=null) sessions.remove((Long)uidObj);
    }
}
