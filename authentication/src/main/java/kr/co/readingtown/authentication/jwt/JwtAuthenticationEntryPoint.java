package kr.co.readingtown.authentication.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.readingtown.common.exception.ErrorCode;
import kr.co.readingtown.common.exception.httpError.HttpErrorCode;
import kr.co.readingtown.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException {

        responseToClient(response, HttpErrorCode.UNAUTHORIZED);
    }

    private void responseToClient(HttpServletResponse response, ErrorCode errorCode) throws IOException {

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        CommonResponse<Integer> errorResponse = new CommonResponse<>(errorCode);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
