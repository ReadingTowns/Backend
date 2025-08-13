package kr.co.readingtown.review.exception;

import kr.co.readingtown.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements ErrorCode {

    REVIEW_NOT_FOUND(5001, "해당 책에 대한 회원의 리뷰가 존재하지 않습니다."),
    REVIEW_ALREADY_EXISTS(5002, "해당 책에 대해 이미 작성한 리뷰가 존재합니다."),
    REVIEW_AUTHOR_MISMATCH(5003, "리뷰 작성자와 요청한 회원이 일치하지 않습니다."),

    REVIEW_MEMBER_NOT_FOUND(5010, "해당 회원은 존재하지 않습니다."),
    REVIEW_BOOK_NOT_FOUND(5011, "해당 책은 존재하지 않습니다.");

    private final int errorCode;
    private final String errorMessage;
}
