package kr.co.readingtown.bookhouse.dto.response;

public record BookPreviewResponseDto(
        Long bookhouseId,
        Long bookId,
        String bookImage,
        String bookName,
        String author
) {
}
