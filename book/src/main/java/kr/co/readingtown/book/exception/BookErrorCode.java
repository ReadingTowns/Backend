package kr.co.readingtown.book.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BookErrorCode implements ErrorCode {

    BOOK_NOT_FOUND(6001, "해당 책은 존재하지 않습니다.");

    private final int errorCode;
    private final String errorMessage;
}
