package org.spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestControllerWS {

    @GetMapping("/chat")
    public String testChat(){
        return "chat.html";
    }

    @GetMapping("/room")
    public String testRoom(){
        return "room-test.html";
    }

    @GetMapping("/game")
    public String testGame(){
        return "game.html";
    }
}
