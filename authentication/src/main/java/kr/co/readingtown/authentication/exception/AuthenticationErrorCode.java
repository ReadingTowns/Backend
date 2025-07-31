package kr.co.readingtown.authentication.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthenticationErrorCode implements ErrorCode {

    TOKEN_NOT_VALID(3001, "유효하지 않은 토큰입니다."),
    NO_TOKEN_EXCEPTION(3002, "토큰이 없습니다"),
    EXPIRED_TOKEN(3003, "만료된 토큰입니다.");

    private final int errorCode;
    private final String errorMessage;
}
