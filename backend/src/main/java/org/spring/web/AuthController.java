package org.spring.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.spring.dto.ErrorDto;
import org.spring.dto.PersonDto;
import org.spring.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("v1/api/auth")
public class AuthController {

    private final PersonService personService;

    @PostMapping("/register")
    @Operation(summary = "create person", tags = {"create"},
            responses = {
                    @ApiResponse(
                            description = "person created",
                            responseCode = "201",
                            content = @Content(schema = @Schema(implementation = PersonDto.class))
                    ),
                    @ApiResponse(
                            description = "username already registered",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = ErrorDto.class))
                    ),
                    @ApiResponse(
                            description = "unknown error",
                            responseCode = "500",
                            content = @Content()
                    )
            })
    public PersonDto registerUser (@RequestBody PersonDto dto){
        return personService.save(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody PersonDto dto){
       return personService.login(dto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refresh){
        return personService.refresh(refresh);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@CookieValue(name = "refresh_token", required = false) String refresh){
        return  personService.logout(refresh);
    }
}

