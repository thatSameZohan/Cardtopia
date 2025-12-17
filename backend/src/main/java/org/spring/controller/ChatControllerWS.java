package org.spring.controller;

import org.spring.dto.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatControllerWS {

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Message sendGlobal(String text, Principal principal) {
        return new Message(principal.getName(),text);
    }
}
