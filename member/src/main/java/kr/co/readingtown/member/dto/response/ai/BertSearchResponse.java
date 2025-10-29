package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record BertSearchResponse(
        String query,
        List<BertSearchResult> results,
        String method
) {
    public record BertSearchResult(
            Long bookId,
            String bookName,
            String author,
            String publisher,
            String bookImage,
            Double similarity,
            List<String> matchedKeywords
    ) {}
}