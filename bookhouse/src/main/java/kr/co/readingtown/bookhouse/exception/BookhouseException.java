package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.CustomException;

public class BookhouseException extends CustomException {

    public BookhouseException(final BookhouseErrorCode bookhouseErrorCode) {
        super(bookhouseErrorCode);
    }
}
