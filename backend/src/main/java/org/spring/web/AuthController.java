package org.spring.web;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.exc.UserCommonException;
import org.spring.model.PersonEntity;
import org.spring.model.RefreshToken;
import org.spring.repository.PersonRepository;
import org.spring.service.impl.JwtService;
import org.spring.service.impl.RefreshTokenService;
import org.spring.util.CookieUtil;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    record RegisterRequest(String username, String email, String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (personRepository.existsByUsername(req.username())) {
            throw new UserCommonException(409,"Пользователь уже существует");
        }
        PersonEntity person = new PersonEntity();
        person.setUsername(req.username());
        person.setPassword(passwordEncoder.encode(req.password()));
        person.setRoles(Set.of("USER"));
        personRepository.save(person);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    record LoginRequest(String username, String password) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {

        PersonEntity person = personRepository.findByUsername(req.username())
                .orElseThrow(() -> new UserCommonException(400,"Такой пользователь не существует"));

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.username(), req.password())
        );

        Map<String, Object> claims = Map.of("roles", person.getRoles());
        String accessToken = jwtService.generateAccessToken(person.getUsername(), claims);

        // generate refresh
        RefreshToken rt = refreshTokenService.createRefreshToken(person);

        // put refresh token in http-only cookie
        CookieUtil.addRefreshTokenCookie(response, rt.getToken());

        // return ONLY access token
        return ResponseEntity.ok(Map.of(
                "accessToken", accessToken
        ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshTokenFromCookie,
            HttpServletResponse response
    ) {
        if (refreshTokenFromCookie == null) {
            throw new UserCommonException(401,"Refresh token не был передан");
        }

        RefreshToken oldRt = refreshTokenService.findByToken(refreshTokenFromCookie)
                .orElseThrow(() -> new UserCommonException(403,"Refresh token не существует"));

        if (oldRt.isRevoked() || refreshTokenService.isExpired(oldRt)) {
            throw new UserCommonException(440,"Refresh token отозван или истек");
        }

        PersonEntity person = oldRt.getPerson();

        // rotate tokens
        refreshTokenService.revokeToken(oldRt);
        RefreshToken newRt = refreshTokenService.createRefreshToken(person);

        // set cookie again
        CookieUtil.addRefreshTokenCookie(response, newRt.getToken());

        // generate new access token
        Map<String, Object> claims = Map.of("roles", person.getRoles());
        String newAccessToken = jwtService.generateAccessToken(person.getUsername(), claims);

        return ResponseEntity.ok(Map.of(
                "accessToken", newAccessToken
        ));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshTokenFromCookie,
            HttpServletResponse response
    ) {

        if (refreshTokenFromCookie == null) {
            throw new UserCommonException(400,"Refresh token не был передан");
        }

        RefreshToken Rt = refreshTokenService.findByToken(refreshTokenFromCookie)
                .orElseThrow(() -> new UserCommonException(403,"Refresh token не существует"));

        if (Rt.isRevoked() || refreshTokenService.isExpired(Rt)) {
            throw new UserCommonException(403,"Refresh token отозван или истек");
        }

        refreshTokenService.revokeToken(Rt);

        CookieUtil.clearRefreshToken(response);

        return ResponseEntity.ok().build();
    }
}
