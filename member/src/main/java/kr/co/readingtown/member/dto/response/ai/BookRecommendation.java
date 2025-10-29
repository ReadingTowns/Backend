package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record BookRecommendation(
        Long bookId,
        String bookImage,
        String bookName,
        String author,
        String publisher,
        String keyword,
        Double similarity,
        List<String> reviewKeywords,
        List<String> relatedUserKeywords
) {
}
