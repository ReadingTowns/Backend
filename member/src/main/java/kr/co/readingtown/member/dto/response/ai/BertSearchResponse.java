package kr.co.readingtown.member.dto.response.ai;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BertSearchResponse(
        String query,
        List<BertSearchResult> results,
        String method
) {
    public record BertSearchResult(
            @JsonProperty("book_id")
            Long bookId,
            @JsonProperty("book_name")
            String bookName,
            String author,
            String publisher,
            @JsonProperty("image")
            String bookImage,
            @JsonProperty("similarity_score")
            Double similarity,
            String keywords,
            @JsonProperty("review_preview")
            String reviewPreview
    ) {}
}