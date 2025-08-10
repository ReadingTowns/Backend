package kr.co.readingtown.book.exception;

import kr.co.readingtown.common.exception.CustomException;

public class BookException extends CustomException {

    public BookException(final BookErrorCode bookErrorCode) {
        super(bookErrorCode);
    }

    public static class BookNotFound extends BookException {
        public BookNotFound() {
            super(BookErrorCode.BOOK_NOT_FOUND);
        }
    }
}
