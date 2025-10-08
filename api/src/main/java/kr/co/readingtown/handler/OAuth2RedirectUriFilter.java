package kr.co.readingtown.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OAuth2RedirectUriFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        if (requestURI.equals("/oauth2/authorization/google")) {
            String redirectUri = request.getParameter("redirect_uri");

            if (redirectUri != null) {
                Cookie cookie = new Cookie("redirect_uri", redirectUri);
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                cookie.setMaxAge(180); // 3분 유효
                response.addCookie(cookie);
            }
        }
        filterChain.doFilter(request, response);
    }
}
