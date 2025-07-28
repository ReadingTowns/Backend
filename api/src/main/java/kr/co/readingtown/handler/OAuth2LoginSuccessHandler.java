package kr.co.readingtown.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readingtown.authentication.domain.CustomOAuth2User;
import kr.co.readingtown.authorization.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = tokenProvider.createAccessToken(oauthUser.getProvider(), oauthUser.getName());
        String refreshToken = tokenProvider.createRefreshToken(oauthUser.getProvider(), oauthUser.getName());

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

        response.sendRedirect("/success.html");
    }
}
