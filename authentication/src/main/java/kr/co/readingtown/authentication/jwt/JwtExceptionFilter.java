package kr.co.readingtown.authentication.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readingtown.authentication.exception.AuthenticationException;
import kr.co.readingtown.common.exception.CustomException;
import kr.co.readingtown.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            filterChain.doFilter(request, response);
        } catch (AuthenticationException e) {
            responseToClient(response, e);
        }
    }

    private void responseToClient(HttpServletResponse response, CustomException e) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        CommonResponse<Integer> errorResponse = new CommonResponse<>(e.getErrorCodeEnum());
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
