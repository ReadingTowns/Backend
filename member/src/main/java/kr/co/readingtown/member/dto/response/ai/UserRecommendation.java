package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record UserRecommendation(
    Long memberId,
    Double similarity,
    List<String> matchedKeywords,
    List<MatchedBook> matchedBooks,
    String matchType
) {
    public record MatchedBook(
        Long bookId,
        String bookName
    ) {}
}