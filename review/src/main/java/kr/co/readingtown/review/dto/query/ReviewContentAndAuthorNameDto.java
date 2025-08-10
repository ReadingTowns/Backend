package kr.co.readingtown.review.dto.query;

public record ReviewContentAndAuthorNameDto(
        Long memberId,
        String content
) {
}
