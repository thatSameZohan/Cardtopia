package org.spring.controller;

import org.spring.dto.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatControllerWS {

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Message sendGlobal(Message message) {
        return message;
    }
}
