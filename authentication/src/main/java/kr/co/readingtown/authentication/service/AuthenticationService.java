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
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";

    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        // 1. 쿠키에서 refresh token 추리
        String refreshToken = cookieUtil.extractTokenFromCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        if (refreshToken == null)
            throw new AuthenticationException.NoTokenException();

        // 2. refresh token 유효성 검사
        tokenProvider.validateRefreshToken(refreshToken);

        // 3. 토큰에서 사용자 정보 추출
        String provider = tokenProvider.getProvider(refreshToken);
        String providerId = tokenProvider.getProviderId(refreshToken);

        // 4. 새 access/refresh token 생성
        String newAccessToken = tokenProvider.createAccessToken(provider, providerId);
        String newRefreshToken = tokenProvider.createRefreshToken(provider, providerId);

        // 5. 쿠키에 새 token 저장
        cookieUtil.saveTokenToCookie(response, newAccessToken, newRefreshToken);
    }
}
