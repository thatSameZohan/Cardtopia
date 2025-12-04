package org.spring.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class CookieUtil {

    public static Cookie addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refresh_token", token);
        cookie.setHttpOnly(true); // true-кука недоступна из JavaScript на стороне клиента, повышает безопасность
        cookie.setSecure(false);       // true-кука будет передаваться только по HTTPS
        cookie.setPath("/api/auth/refresh"); // кука отправляется только сюда
        cookie.setMaxAge(60 * 60 * 24 * 1); // время жизни куки 1 день
        cookie.setDomain("localhost"); //  домен, для которого действительна кука
        cookie.setAttribute("SameSite", "Lax");
        // для SameSite=None обязательно наличие Secure=true (HTTPS), иначе браузер может игнорировать куку.
        // SameSite=Lax (базовая защита) или SameSite=Strict (максимальная защита).
        response.addCookie(cookie);
        return cookie;
    }

    public static void clearRefreshToken(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/api/auth/**");
        cookie.setMaxAge(0);
        cookie.setAttribute("SameSite", "Lax");
        response.addCookie(cookie);
    }
}
