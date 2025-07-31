package kr.co.readingtown.member.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MISSING_REQUIRED_FIELD(4001,"온보딩 필수 입력값이 누락되었습니다." ),
    NO_AUTH_MEMBER(3104, "로그인된 회원 정보가 없습니다.");

    private final int errorCode;
    private final String errorMessage;
};
