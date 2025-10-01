package kr.co.readingtown.member.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KeywordErrorCode implements ErrorCode {

    NOT_FOUND_KEYWORD(10001, "해당 키워드 정보를 찾을 수 없습니다.");

    private final int errorCode;
    private final String errorMessage;
}
