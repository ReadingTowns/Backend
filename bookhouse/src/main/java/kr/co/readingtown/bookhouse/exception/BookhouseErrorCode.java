package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookhouseErrorCode implements ErrorCode {

    BOOKHOUSE_NOT_FOUND(7001, "서재에 해당 책에 대한 정보가 존재하지 않습니다."),
    BOOKHOUSE_ALREADY_EXISTS(7002, "서재에 해당 책이 이미 존재합니다.");

    private final int errorCode;
    private final String errorMessage;
}
