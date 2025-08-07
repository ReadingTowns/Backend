package kr.co.readingtown.common.exception.httpError;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HttpErrorCode implements ErrorCode {

    INTERNAL_SERVER_ERROR(2001, "요청 처리 중 알 수 없는 오류가 발생했습니다."),
    UNAUTHORIZED(2002, "인증이 필요한 요청입니다."),
    BAD_REQUEST(2003, "잘못된 요청입니다."),
    METHOD_NOT_ALLOWED(2004, "허용되지 않는 HTTP 메서드입니다."),
    VALIDATION_FAILED(2005, "입력값 검증에 실패했습니다."),
    MISSING_PARAMETER(2006, "필수 요청 파라미터가 누락되었습니다."),
    MESSAGE_NOT_READABLE(2007, "요청 메시지를 읽을 수 없습니다."),
    CONSTRAINT_VIOLATION(2008, "유효성 제약 조건 위반입니다."),
    ACCESS_DENIED(2009, "접근 권한이 없습니다."),
    NOT_FOUND(2010, "요청하신 자원을 찾을 수 없습니다."),
    DATA_CONFLICT(2011, "데이터 충돌이 발생했습니다.");

    private final int errorCode;
    private final String errorMessage;
}
