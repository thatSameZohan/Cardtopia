package org.spring.ws;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.spring.security.JwtUtil;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwt;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest sreq){
            HttpServletRequest servlet = sreq.getServletRequest();
            String token = null;
            Cookie[] cookies = servlet.getCookies();
            if (cookies!=null){
                for (Cookie c: cookies) if ("refresh_token".equals(c.getName())) token = c.getValue();
            }
            if (token==null) token = servlet.getParameter("access_token");
            if (token==null) return false;
            try{
                Jws<Claims> claims = jwt.parse(token);
                Long uid = Long.valueOf(claims.getBody().getSubject());
                attributes.put("userId", uid);
                attributes.put("username", claims.getBody().get("username"));
                return true;
            }catch(Exception e){return false;}
        }
        return false;
    }

    @Override public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {}
}