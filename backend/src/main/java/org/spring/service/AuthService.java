package org.spring.service;

import jakarta.servlet.http.HttpServletResponse;
import org.spring.dto.AuthResponse;

public interface AuthService {

    void register(String username, String password);

    AuthResponse login(String username, String password, HttpServletResponse response);

    AuthResponse refreshToken(String refreshTokenFromCookie, HttpServletResponse response);

    void logout(String refreshTokenFromCookie, HttpServletResponse response);
}
