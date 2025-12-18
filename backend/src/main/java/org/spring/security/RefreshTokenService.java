package org.spring.security;

import org.spring.model.PersonEntity;
import org.spring.model.RefreshToken;
import org.spring.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshTokenMs;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${jwt.refresh-ms}") long refreshTokenMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenMs = refreshTokenMs;
    }

    public RefreshToken createRefreshToken(PersonEntity person) {
        RefreshToken token = new RefreshToken();
        token.setPerson(person);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(refreshTokenMs));
        token.setRevoked(false);
        return refreshTokenRepository.save(token);
//        createRefreshToken генерирует UUID-based token и сохраняет его в БД.
//        Если нужен JWT формат для refresh,
//        можно вместо UUID генерировать JWT через JwtService и сохранять строку.
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isExpired(RefreshToken token) {
        return token.getExpiryDate().isBefore(Instant.now());
    }

    public void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    public void deleteByPerson(PersonEntity person) {
        refreshTokenRepository.deleteByPerson(person);
    }
}
