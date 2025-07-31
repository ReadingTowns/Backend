package kr.co.readingtown.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readingtown.authentication.domain.CustomOAuth2User;
import kr.co.readingtown.authentication.jwt.TokenProvider;
import kr.co.readingtown.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();

        String accessToken = tokenProvider.createAccessToken(oauthUser.getProvider(), oauthUser.getName());
        String refreshToken = tokenProvider.createRefreshToken(oauthUser.getProvider(), oauthUser.getName());

        cookieUtil.saveTokenToCookie(response, accessToken, refreshToken);

        response.sendRedirect("/success.html");
    }
}
