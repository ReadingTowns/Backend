package kr.co.readingtown.review.dto.query;

public record ReviewIdAndContentDto(
        Long reviewId,
        String content
) {
}
