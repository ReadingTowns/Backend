package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.CustomException;

public class BookhouseException extends CustomException {

    public BookhouseException(final BookhouseErrorCode bookhouseErrorCode) {
        super(bookhouseErrorCode);
    }

    public static class BookhouseCreateFailed extends BookhouseException {
        public BookhouseCreateFailed() {
            super(BookhouseErrorCode.BOOKHOUSE_CREATE_FAILED);
        }
    }

    public static class BookhouseNotFound extends BookhouseException {
        public BookhouseNotFound() {
            super(BookhouseErrorCode.BOOKHOUSE_NOT_FOUND);
        }
    }
}
