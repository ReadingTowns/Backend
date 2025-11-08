package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record BookSearchResponseDto(
        Long bookId,
        String bookImage,
        String bookName,
        String author,
        String publisher,
        Double similarity,
        List<String> relatedUserKeywords,
        String reviewPreview
) {
}