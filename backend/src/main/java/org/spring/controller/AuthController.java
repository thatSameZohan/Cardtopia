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
import org.spring.dto.LoginRequest;
import org.spring.dto.RegisterRequest;
import org.spring.service.AuthService;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "register user",
            responses = {
                    @ApiResponse(description = "Пользователь зарегистрирован", responseCode = "201", content = @Content()),
                    @ApiResponse(description = "Пользователь уже существует", responseCode = "409", content = @Content(schema = @Schema(implementation = ErrorDto.class)))
            })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        authService.register(req.username(), req.password());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "login user",
            responses = {
                    @ApiResponse(description = "Пользователь авторизован", responseCode = "200", content = @Content(schema = @Schema(implementation = AuthResponse.class)))
            })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        return ResponseEntity.ok(authService.login(req.username(), req.password(), response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                                HttpServletResponse response) {
        return ResponseEntity.ok(authService.refreshToken(refreshToken, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "refresh_token", required = false) String refreshToken,
                                       HttpServletResponse response) {
        authService.logout(refreshToken, response);
        return ResponseEntity.noContent().build();
    }
}
