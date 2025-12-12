package org.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.spring.dto.AuthResponse;
import org.spring.dto.ErrorDto;
import org.spring.dto.MeResponse;
import org.spring.exc.UserCommonException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/me")
public class MeController {

    @Operation(summary = "authenticated access",
            responses = {
                    @ApiResponse(
                            description = "Доступ authenticated получен",
                            responseCode = "200",
                            content = @Content(schema = @Schema(implementation = MeResponse.class))
                    ),
                    @ApiResponse(
                            description = "Пользователь не авторизован",
                            responseCode = "401",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "Неизвестная ошибка",
                            responseCode = "500",
                            content = @Content()
                    )
            })
    @GetMapping
    public MeResponse getCurrentUser (@AuthenticationPrincipal UserDetails user) {

        if (user == null) {
            throw new UserCommonException(401, "Пользователь не авторизован");
        }
        return new  MeResponse(user.getUsername());
    }

}
