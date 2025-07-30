package kr.co.readingtown.authentication.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readingtown.authentication.exception.AuthenticationException;
import kr.co.readingtown.authentication.jwt.TokenProvider;
import kr.co.readingtown.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final TokenProvider tokenProvider;
    private final CookieUtil cookieUtil;

    // accessToken 재발급 할 때 refreshToken도 함께 재발급
    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = cookieUtil.extractTokenFromCookie(request, "refresh_token");

        // TODO: refreshToken 검증 로직 추가 (category 검사 + redis 확인)
        try {

            if (refreshToken != null & tokenProvider.validateToken(refreshToken)) {

                String provider = tokenProvider.getProvider(refreshToken);
                String providerId = tokenProvider.getProviderId(refreshToken);

                String newAccessToken = tokenProvider.createAccessToken(provider, providerId);
                String newRefreshToken = tokenProvider.createRefreshToken(provider, providerId);

                cookieUtil.saveTokenToCookie(response, newAccessToken, newRefreshToken);
            }
        } catch (AuthenticationException e) {
            throw e;
        }
    }
}
