package kr.co.readingtown.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

@Component
public class OAuth2RedirectUriFilter extends OncePerRequestFilter {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/oauth2/authorization/google")) {
            String redirectUri = request.getParameter("redirect_uri");

            if (redirectUri != null && isValidRedirectUri(redirectUri)) {
                Cookie cookie = new Cookie("redirect_uri", redirectUri);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(180); // 3분 유효
                
                // Origin에 따라 쿠키 도메인 설정
                String origin = request.getHeader("Origin");
                if (origin != null && isLocalhost(origin)) {
                    // localhost의 경우 도메인 설정하지 않음
                } else {
                    cookie.setDomain(".readingtown.site");
                }
                
                response.addCookie(cookie);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isValidRedirectUri(String redirectUri) {
        try {
            URI uri = new URI(redirectUri);
            String host = uri.getHost();
            
            // 허용된 호스트 목록
            List<String> allowedHosts = Arrays.asList(
                "localhost",
                "127.0.0.1", 
                "readingtown.site"
            );
            
            return allowedHosts.contains(host);
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
    private boolean isLocalhost(String origin) {
        return origin != null && (origin.contains("localhost") || origin.contains("127.0.0.1"));
    }
}
