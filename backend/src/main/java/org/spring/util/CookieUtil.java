package org.spring.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static Cookie addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);       // обязательно для HTTPS
        cookie.setPath("/api/auth/refresh"); // кука отправляется только сюда
        cookie.setMaxAge(60 * 60 * 24 * 1); // 1 день
        cookie.setDomain("localhost"); // подстрой под свое окружение
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
        return cookie;
    }

    public static void clearRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/refresh");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }
}
