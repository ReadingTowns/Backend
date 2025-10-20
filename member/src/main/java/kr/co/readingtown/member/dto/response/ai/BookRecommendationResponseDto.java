package kr.co.readingtown.member.dto.response.ai;

public record BookRecommendationResponseDto(
        Long bookId,
        String bookName,
        String author,
        String publisher,
        Double similarity
) {
}
