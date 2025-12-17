package org.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.spring.dto.AuthResponse;
import org.spring.dto.ErrorDto;
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

    record RegisterRequest(String username, String password) {}

    @Operation(summary = "register user",
            responses = {
                    @ApiResponse(
                            description = "Пользователь зарегистрирован",
                            responseCode = "201",
                            content = @Content()
                    ),
                    @ApiResponse(
                            description = "Пользователь уже существует",
                            responseCode = "409",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Неизвестная ошибка",
                            responseCode = "500",
                            content = @Content()
                    )
            })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (personRepository.existsByUsername(req.username())) {
            throw new UserCommonException(409, "Пользователь уже существует");
        }
        PersonEntity person = new PersonEntity();
        person.setUsername(req.username());
        person.setPassword(passwordEncoder.encode(req.password()));
        person.setRoles(Set.of("USER"));
        personRepository.save(person);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    record LoginRequest(String username, String password) {
    }

    @Operation(summary = "login user",
            responses = {
                    @ApiResponse(
                            description = "Пользователь авторизован",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(
                            description = "Пользователь не существует",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Неизвестная ошибка",
                            responseCode = "500",
                            content = @Content()
                    )
            })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {

        PersonEntity person = personRepository.findByUsername(req.username())
                .orElseThrow(() -> new UserCommonException(400, "Такой пользователь не существует"));

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
        return ResponseEntity.ok(new AuthResponse(accessToken));
    }

    @Operation(summary = "refresh token",
            responses = {
                    @ApiResponse(
                            description = "Успешное обновление refresh и access токенов",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class))
                    ),
                    @ApiResponse(
                            description = "Refresh token не был передан",
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Refresh token не существует",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Refresh token отозван или истек",
                            responseCode = "440",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Неизвестная ошибка",
                            responseCode = "500",
                            content = @Content()
                    )
            })
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(value = "refresh_token", required = false) String refreshTokenFromCookie,
            HttpServletResponse response
    ) {
        if (refreshTokenFromCookie == null) {
            throw new UserCommonException(401, "Refresh token не был передан");
        }

        RefreshToken oldRt = refreshTokenService.findByToken(refreshTokenFromCookie)
                .orElseThrow(() -> new UserCommonException(403, "Refresh token не существует"));

        if (oldRt.isRevoked() || refreshTokenService.isExpired(oldRt)) {
            throw new UserCommonException(440, "Refresh token отозван или истек");
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

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @Operation(summary = "logout",
            responses = {
                    @ApiResponse(
                            description = "Успешный logout",
                            responseCode = "204",
                            content = @Content()
                    ),
                    @ApiResponse(
                            description = "Refresh token не был передан",
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Refresh token не существует",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Refresh token отозван или истек",
                            responseCode = "440",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Неизвестная ошибка",
                            responseCode = "500",
                            content = @Content()
                    )
            })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(value = "refresh_token", required = false) String refreshTokenFromCookie,
            HttpServletResponse response
    ) {

        if (refreshTokenFromCookie == null) {
            throw new UserCommonException(401, "Refresh token не был передан");
        }

        RefreshToken Rt = refreshTokenService.findByToken(refreshTokenFromCookie)
                .orElseThrow(() -> new UserCommonException(403, "Refresh token не существует"));

        if (Rt.isRevoked() || refreshTokenService.isExpired(Rt)) {
            throw new UserCommonException(440, "Refresh token отозван или истек");
        }

        refreshTokenService.revokeToken(Rt);

        CookieUtil.clearRefreshToken(response);

        return ResponseEntity.noContent().build();
    }
}
