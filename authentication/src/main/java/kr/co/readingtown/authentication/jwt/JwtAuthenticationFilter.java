package kr.co.readingtown.authentication.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readingtown.authentication.client.MemberClient;
import kr.co.readingtown.authentication.exception.AuthenticationException;
import kr.co.readingtown.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CookieUtil cookieUtil;
    private final MemberClient memberClient;
    private final TokenProvider tokenProvider;
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 쿠키에서 access token 추출
        String token = cookieUtil.extractTokenFromCookie(request, ACCESS_TOKEN_COOKIE_NAME);
        if (token == null) {
            throw new AuthenticationException.NoTokenException();
        }

        // 2. access token 유효성 검사
        tokenProvider.validateAccessToken(token);

        // 3. 토큰에서 사용자 식별 정보 추출
        String provider = tokenProvider.getProvider(token);
        String providerId = tokenProvider.getProviderId(token);
        Long memberId = memberClient.getMemberId(provider, providerId);

        // 4. Authentication 객체 생성 및 SecurityContext 저장
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(memberId, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 5. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return path.startsWith("/favicon.ico")
                || path.startsWith("/static/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/public/")
                || path.startsWith("/error")
                || path.startsWith("/health")
                || path.startsWith("/.well-known/");
    }
}
