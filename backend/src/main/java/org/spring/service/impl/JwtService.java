package org.spring.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {

    private final Key key;
    private final long accessTokenMs;
    private final long refreshTokenMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-ms}") long accessTokenMs,
            @Value("${jwt.refresh-ms}") long refreshTokenMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.accessTokenMs = accessTokenMs;
        this.refreshTokenMs = refreshTokenMs;
    }

    public String generateAccessToken(String subject, Map<String, Object> extraClaims) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token)
                .getBody().getSubject();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Authentication getAuthentication(String token) {
        try {
            Claims claims = extractAllClaims(token);

            String username = claims.getSubject();
            if (username == null) {
                return null;
            }

            // Если есть роли в токене, можно их извлечь
            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof List<?> rolesList) {
                authorities = rolesList.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            }

            return new UsernamePasswordAuthenticationToken(username, null, authorities);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
