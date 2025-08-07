package kr.co.readingtown.review.exception;

import kr.co.readingtown.common.exception.CustomException;

public class ReviewException extends CustomException {

    public ReviewException(final ReviewErrorCode reviewErrorCode) {
        super(reviewErrorCode);
    }

    public static class ReviewNotFound extends ReviewException {
        public ReviewNotFound() {
            super(ReviewErrorCode.REVIEW_NOT_FOUND);
        }
    }

    public static class ReviewAlreadyExists extends ReviewException {
        public ReviewAlreadyExists() {
            super(ReviewErrorCode.REVIEW_ALREADY_EXISTS);
        }
    }
}
