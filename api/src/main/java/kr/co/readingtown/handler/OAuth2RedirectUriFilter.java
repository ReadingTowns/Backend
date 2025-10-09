package kr.co.readingtown.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class OAuth2RedirectUriFilter extends OncePerRequestFilter {

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/oauth2/authorization/google") || requestURI.equals("/oauth2/authorization/kakao")) {
            String redirectUri = request.getParameter("redirect_uri");

            if (redirectUri != null && isValidRedirectUri(redirectUri)) {

                log.info("===save redirect uri in cookie===");
                log.info("redirect_uri = {}", redirectUri);

                ResponseCookie cookie = ResponseCookie.from("redirect_uri", redirectUri)
                        .domain(".readingtown.site")
                        .path("/")
                        .httpOnly(true)
                        .secure(true)
                        .maxAge(Duration.ofMinutes(3))
                        .build();
                
                response.addHeader("Set-Cookie", cookie.toString());
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
                "readingtown.site",
                "dev.readingtown.site"
            );
            
            return allowedHosts.contains(host);
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
}
