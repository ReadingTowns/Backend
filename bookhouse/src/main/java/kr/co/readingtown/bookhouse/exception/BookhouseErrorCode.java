package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookhouseErrorCode implements ErrorCode {

    BOOKHOUSE_CREATE_FAILED(5001,"서재 생성에 실패하였습니다." ),
    BOOKHOUSE_NOT_FOUND(5002, "서재가 존재하지 않습니다.");

    private final int errorCode;
    private final String errorMessage;
};