package kr.readingtown.backend.exception.httpError;

import kr.readingtown.backend.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(2001, "요청 처리 중 알 수 없는 오류가 발생했습니다.");

    private final int errorCode;
    private final String errorMessage;
}
