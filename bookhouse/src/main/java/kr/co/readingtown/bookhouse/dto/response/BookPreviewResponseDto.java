package kr.co.readingtown.bookhouse.dto.response;

public record BookPreviewResponseDto(
        Long id,
        String image,
        String title,
        String authorName
) {
}
