package org.spring.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/me")
public class MeController {

    @GetMapping
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        return Map.of(
                "username", user.getUsername()
        );
    }

}
