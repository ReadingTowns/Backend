package kr.co.readingtown.common.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kr.co.readingtown.common.exception.ErrorCode;
import kr.co.readingtown.common.exception.httpError.HttpErrorCode;

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

    // 비즈니스 로직에서 정의한 커스텀 예외(ErrorCode) 발생 시 실패 응답 생성
    public CommonResponse(ErrorCode errorCode) {
        this(LocalDateTime.now(), errorCode.getErrorCode(), errorCode.getErrorMessage(), null);
    }

    // 시스템 또는 외부 예외(Exception) 발생 시 실패 응답 생성
    public CommonResponse(Exception e) {
        this(LocalDateTime.now(), 2001, e.getMessage(), null);
    }
}
