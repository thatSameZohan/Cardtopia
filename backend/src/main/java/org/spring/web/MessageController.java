package org.spring.web;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {

    @MessageMapping("/hello") // /app/send
    @SendTo("/topic/greetings") // куда отправлять всем подписчикам
    public String greeting(String message){
        return "Server says: " + message;
    }
    // Теперь сервер слушает /ws и умеет принимать сообщения на /app/hello и рассылать их на /topic/greetings.
}
