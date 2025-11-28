package org.spring.config;

import org.spring.security.JwtUtil;
import org.spring.ws.GameWebSocketHandler;
import org.spring.ws.JwtHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    GameWebSocketHandler gameHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gameHandler, "/ws2")
                .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
                .setAllowedOrigins("*");
    }
}