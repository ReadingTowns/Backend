package kr.co.readingtown.bookhouse.exception;

import kr.co.readingtown.common.exception.CustomException;

public class BookhouseException extends CustomException {

    public BookhouseException(final BookhouseErrorCode bookhouseErrorCode) {
        super(bookhouseErrorCode);
    }

    public static class BookhouseNotFound extends BookhouseException {
        public BookhouseNotFound() {
            super(BookhouseErrorCode.BOOKHOUSE_NOT_FOUND);
        }
    }

    public static class BookhouseAlreadyExists extends BookhouseException {
        public BookhouseAlreadyExists() {
            super(BookhouseErrorCode.BOOKHOUSE_ALREADY_EXISTS);
        }
    }
}
