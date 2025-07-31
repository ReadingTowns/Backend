package kr.co.readingtown.authentication.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthenticationErrorCode implements ErrorCode {

    TOKEN_NOT_VALID(3001, "유효하지 않은 토큰입니다."),
    INVALID_ACCESS_TOKEN(3002, "유효하지 않은 Access Token 입니다."),
    INVALID_REFRESH_TOKEN(3003, "유효하지 않은 Refresh Token 입니다."),
    NO_TOKEN_EXCEPTION(3004, "토큰이 없습니다"),
    EXPIRED_TOKEN(3005, "만료된 토큰입니다."),
    TOKEN_CATEGORY_MISMATCH(3006, "토큰 타입이 일치하지 않습니다."),
    REFRESH_TOKEN_NOT_FOUND(3007, "해당 사용자의 Refresh Token이 만료되었거나 삭제되었습니다."),
    BLACKLISTED_TOKEN(3008, "블랙리스트 된 토큰입니다.");

    private final int errorCode;
    private final String errorMessage;
}
