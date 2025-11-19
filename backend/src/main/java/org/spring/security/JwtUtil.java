package org.spring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    private final Key key;
    private final long accessMs;
    private final long refreshMs;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-ms}") long accessMs,
                   @Value("${jwt.refresh-ms}") long refreshMs) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessMs = accessMs;
        this.refreshMs = refreshMs;
    }

    public String createAccessToken(Long userId, String username){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("username", username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+accessMs))
                .signWith(key).compact();
    }
    public String createRefreshToken(Long userId){
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+refreshMs))
                .signWith(key).compact();
    }
    public Jws<Claims> parse(String token){
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
    }
    public long getRefreshMs(){return refreshMs;}
}
