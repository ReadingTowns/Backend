package kr.co.readingtown.review.dto.response;

import kr.co.readingtown.review.domain.Review;
import kr.co.readingtown.review.dto.query.ReviewIdAndContentDto;

public record ReviewResponseDto(
        Long reviewId,
        String content
) {

    public static ReviewResponseDto toReviewResponseDto(Review review) {

        return new ReviewResponseDto(
                review.getReviewId(),
                review.getContent()
        );
    }

    public static ReviewResponseDto toReviewResponseDto(ReviewIdAndContentDto dto) {

        return new ReviewResponseDto(
                dto.reviewId(),
                dto.content()
        );
    }
}
