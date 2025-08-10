package kr.co.readingtown.common.log;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.readingtown.common.exception.CustomException;

import java.util.Arrays;

public record LogInfo(
        String method,
        String uri,
        String message,
        String location
) {

    public static LogInfo from(Exception e, HttpServletRequest request) {

        // 오류 메시지
        String formatMessage;
        if (e instanceof CustomException ce)
            formatMessage = ce.getErrorMessage();
        else
            formatMessage = e.getMessage();

        // 오류 발생 위치
        String formatLocation = Arrays.stream(e.getStackTrace())
                .findFirst()
                .map(StackTraceElement::toString)
                .orElse("no stack");

        return new LogInfo(
                request.getMethod(),
                request.getRequestURI(),
                formatMessage,
                formatLocation
        );
    }
}
