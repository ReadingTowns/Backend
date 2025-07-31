package kr.co.readingtown.authentication.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readingtown.authentication.exception.AuthenticationException;
import kr.co.readingtown.authentication.jwt.RefreshTokenService;
import kr.co.readingtown.authentication.jwt.TokenBlacklistService;
import kr.co.readingtown.authentication.jwt.TokenProvider;
import kr.co.readingtown.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static kr.co.readingtown.common.util.CookieUtil.ACCESS_TOKEN_COOKIE_NAME;
import static kr.co.readingtown.common.util.CookieUtil.REFRESH_TOKEN_COOKIE_NAME;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthenticationService {

    private final CookieUtil cookieUtil;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final TokenBlacklistService tokenBlacklistService;

    public void reissue(HttpServletRequest request, HttpServletResponse response) {

        // 1. 쿠키에서 refresh token 추출
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

    public void logout(HttpServletRequest request) {

        // 1. 쿠키에서 access token 추출
        String accessToken = cookieUtil.extractTokenFromCookie(request, ACCESS_TOKEN_COOKIE_NAME);

        // 2. 토큰에서 사용자 정보 추출
        String provider = tokenProvider.getProvider(accessToken);
        String providerId = tokenProvider.getProviderId(accessToken);

        // 3. redis에서 refresh token 삭제
        refreshTokenService.deleteRefreshToken(provider, providerId);

        // 4. redis 블랙리스트에 access token 저장
        long expiration = tokenProvider.getExpiration(accessToken);  // access token 남은 유효기간
        tokenBlacklistService.addToBlacklist(accessToken, expiration);
    }
}
