package org.spring.web;

import org.spring.model.Message;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    // receive: /app/send
    @MessageMapping("/send")
    public void send(Message message) {
        messagingTemplate.convertAndSend("/topic/room/" + message.getRoom(), message);
    }
}