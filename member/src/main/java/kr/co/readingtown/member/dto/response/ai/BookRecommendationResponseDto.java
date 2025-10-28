package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record BookRecommendationResponseDto(
        Long bookId,
        String bookImage,
        String bookName,
        String author,
        String publisher,
        Double similarity,
        List<String> relatedUserKeywords
) {
}
