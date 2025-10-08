package kr.co.readingtown.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class CsrfProtectionFilter implements Filter {

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 내부 API 경로는 CSRF 검증 제외
        if (isInternalApiRequest(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        // 수정 요청만 검증 (GET, HEAD, OPTIONS 제외)
        if (isModifyingRequest(httpRequest)) {
            String origin = httpRequest.getHeader("Origin");
            String referer = httpRequest.getHeader("Referer");

            // Origin 또는 Referer가 허용된 도메인이 아니면 차단
            if (!isAllowedOrigin(origin) && !isAllowedReferer(referer)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write("{\"error\":\"CSRF protection: Invalid origin\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isModifyingRequest(HttpServletRequest request) {
        String method = request.getMethod();
        return "POST".equals(method) || "PUT".equals(method) || 
               "DELETE".equals(method) || "PATCH".equals(method);
    }

    private boolean isAllowedOrigin(String origin) {
        if (origin == null) return false;
        return allowedOrigins.contains(origin);
    }

    private boolean isAllowedReferer(String referer) {
        if (referer == null) return false;
        return allowedOrigins.stream()
                .anyMatch(allowed -> referer.startsWith(allowed));
    }

    private boolean isInternalApiRequest(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/internal/");
    }
}