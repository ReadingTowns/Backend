package kr.co.readingtown.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CookieUtil {

    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

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
        Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessToken);
        accessTokenCookie.setDomain(".readingtown.site");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setAttribute("SameSite", "Lax"); //크로스 사이트 요청 시 쿠키 전송 허용
        accessTokenCookie.setMaxAge(15 * 60);

        // refreshToken 쿠키 설정
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, refreshToken);
        refreshTokenCookie.setDomain(".readingtown.site");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setAttribute("SameSite", "Lax");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60);

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
    }
}
