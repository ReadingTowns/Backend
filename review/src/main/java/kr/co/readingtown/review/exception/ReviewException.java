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

    public static class ReviewAuthorMismatch extends ReviewException {
        public ReviewAuthorMismatch() {
            super(ReviewErrorCode.REVIEW_AUTHOR_MISMATCH);
        }
    }

    public static class ReviewMemberNotFound extends ReviewException {
        public ReviewMemberNotFound() {
            super(ReviewErrorCode.REVIEW_MEMBER_NOT_FOUND);
        }
    }

    public static class ReviewBookNotFound extends ReviewException {
        public ReviewBookNotFound() {
            super(ReviewErrorCode.REVIEW_BOOK_NOT_FOUND);
        }
    }
}
