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
    NICKNAME_GENERATION_FAILED(4010, "중복이 많아 기본 닉네임 생성에 실패했습니다."),
    INVALID_NICKNAME_FORMAT(4011, "닉네임은 2자 이상 20자 이하로 입력해주세요."),
    NICKNAME_ALREADY_EXISTS(4012, "이미 사용 중인 닉네임입니다."),

    SELF_RATING_NOT_ALLOWED(4020, "본인에게 유저 평가는 남길 수 없습니다."),

    TOWN_RESOLVED_FAILED(4030, "위치 정보를 해석할 수 없습니다.");
    private final int errorCode;
    private final String errorMessage;
};
