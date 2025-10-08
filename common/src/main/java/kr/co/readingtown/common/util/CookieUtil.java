package kr.co.readingtown.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
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
        saveTokenToCookie(response, accessToken, refreshToken, null);
    }

    public void saveTokenToCookie(HttpServletResponse response, String accessToken, String refreshToken, String origin) {
        // accessToken 쿠키 설정
        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
                .domain(".readingtown.site")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofMinutes(15))
                .build();

        // refreshToken 쿠키 설정
        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .domain(".readingtown.site")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("None")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
