package kr.co.readingtown.social.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FollowErrorCode implements ErrorCode {

    SELF_FOLLOW_NOT_ALLOWED(8001, "자기 자신을 팔로우할 수 없습니다.");

    private final int errorCode;
    private final String errorMessage;
};