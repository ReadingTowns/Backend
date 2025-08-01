package kr.co.readingtown.member.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    MISSING_REQUIRED_FIELD(4001,"온보딩 필수 입력값이 누락되었습니다." ),
    NO_AUTH_MEMBER(4002, "로그인된 회원 정보가 없습니다."),

    //닉네임 관련
    USERNAME_GENERATION_FAILED(4010, "중복이 많아 기본 닉네임 생성에 실패했습니다."),
    INVALID_USERNAME_FORMAT(4011, "닉네임은 2자 이상 20자 이하로 입력해주세요."),
    USERNAME_ALREADY_EXISTS(4012, "이미 사용 중인 닉네임입니다.");
    private final int errorCode;
    private final String errorMessage;
};
