package kr.readingtown.backend.global.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record CommonResponse<T>(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        int code,
        String message,
        T result
) {

    // api 응답이 성공이고, 반환값 있는 경우
    public CommonResponse(T result) {
        this(LocalDateTime.now(), 1000, "Success", result);
    }

    // api 응답이 실패인 경우
    public CommonResponse(int errorCode, String errorMessage) {
        this(LocalDateTime.now(), errorCode, errorMessage, null);
    }
}
