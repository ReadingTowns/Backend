package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookhouseErrorCode implements ErrorCode {

    private final int errorCode;
    private final String errorMessage;
};