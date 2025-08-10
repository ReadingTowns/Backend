package kr.co.readingtown.book.dto.query;

public record BookPreviewDto(
        Long id,
        String image,
        String title,
        String authorName
) {
}
