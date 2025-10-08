package kr.co.readingtown.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

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

        // redirectUri 추출
        String redirectUri = Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{}))
                .filter(cookie -> "redirect_uri".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("https://readingtown.site/auth/callback");

        // Origin 정보를 이용해 적절한 쿠키 설정
        String origin = getOriginFromRedirectUri(redirectUri);
        cookieUtil.saveTokenToCookie(response, accessToken, refreshToken, origin);

        response.sendRedirect(redirectUri);
    }

    private String getOriginFromRedirectUri(String redirectUri) {
        try {
            URI uri = new URI(redirectUri);
            return uri.getScheme() + "://" + uri.getHost() + (uri.getPort() != -1 ? ":" + uri.getPort() : "");
        } catch (URISyntaxException e) {
            return null;
        }
    }
}
