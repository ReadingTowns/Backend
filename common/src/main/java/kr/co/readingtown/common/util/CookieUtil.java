package kr.co.readingtown.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtil {

    // type : access_token, refresh_token
    public String extractTokenFromCookie(HttpServletRequest request, String type) {
        if (request.getCookies() == null) return null;

        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(type))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    public void saveTokenToCookie(HttpServletResponse response, String accessToken, String refreshToken) {

        // accessToken 쿠키 설정
        Cookie accessTokenCookie = new Cookie("access_token", accessToken);
        // accessTokenCookie.setHttpOnly(true);
        // accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60);

        // refreshToken 쿠키 설정
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        // refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
