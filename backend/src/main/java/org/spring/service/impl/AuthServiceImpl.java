package org.spring.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.spring.dto.AuthResponse;
import org.spring.exc.AuthCommonException;
import org.spring.model.PersonEntity;
import org.spring.model.RefreshToken;
import org.spring.repository.PersonRepository;
import org.spring.security.JwtService;
import org.spring.security.RefreshTokenService;
import org.spring.service.AuthService;
import org.spring.util.CookieUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void register(String username, String password) {
        if (personRepository.existsByUsername(username)) {
            throw new AuthCommonException(409, "Пользователь уже существует");
        }
        PersonEntity person = new PersonEntity();
        person.setUsername(username);
        person.setPassword(passwordEncoder.encode(password));
        person.setRoles(Set.of("USER"));
        personRepository.save(person);
    }

    @Override
    public AuthResponse login(String username, String password, HttpServletResponse response) {
        PersonEntity person = personRepository.findByUsername(username)
                .orElseThrow(() -> new AuthCommonException(400, "Такой пользователь не существует"));

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        String accessToken = jwtService.generateAccessToken(username, Map.of("roles", person.getRoles()));
        refreshTokenService.deleteByPerson(person);
        RefreshToken rt = refreshTokenService.createRefreshToken(person);
        CookieUtil.addRefreshTokenCookie(response, rt.getToken());

        return new AuthResponse(accessToken);
    }

    @Override
    public AuthResponse refreshToken(String refreshTokenFromCookie, HttpServletResponse response) {
        if (refreshTokenFromCookie == null) {
            throw new AuthCommonException(401, "Refresh token не был передан");
        }

        RefreshToken oldRt = refreshTokenService.findByToken(refreshTokenFromCookie)
                .orElseThrow(() -> new AuthCommonException(403, "Refresh token не существует"));

        if (oldRt.isRevoked() || refreshTokenService.isExpired(oldRt)) {
            throw new AuthCommonException(440, "Refresh token отозван или истек");
        }

        PersonEntity person = oldRt.getPerson();
        refreshTokenService.deleteByPerson(person);
        RefreshToken newRt = refreshTokenService.createRefreshToken(person);
        CookieUtil.addRefreshTokenCookie(response, newRt.getToken());

        String newAccessToken = jwtService.generateAccessToken(person.getUsername(), Map.of("roles", person.getRoles()));
        return new AuthResponse(newAccessToken);
    }

    @Override
    public void logout(String refreshTokenFromCookie, HttpServletResponse response) {
        if (refreshTokenFromCookie == null) {
            throw new AuthCommonException(401, "Refresh token не был передан");
        }

        RefreshToken rt = refreshTokenService.findByToken(refreshTokenFromCookie)
                .orElseThrow(() -> new AuthCommonException(403, "Refresh token не существует"));

        if (rt.isRevoked() || refreshTokenService.isExpired(rt)) {
            throw new AuthCommonException(440, "Refresh token отозван или истек");
        }

        refreshTokenService.deleteByPerson(rt.getPerson());
        CookieUtil.clearRefreshToken(response);
    }
}
