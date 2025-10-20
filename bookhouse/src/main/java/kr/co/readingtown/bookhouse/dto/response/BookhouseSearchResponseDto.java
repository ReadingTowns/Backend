package kr.co.readingtown.bookhouse.dto.response;

public record BookhouseSearchResponseDto(
        Long bookhouseId,
        Long bookId,
        String bookName,
        String bookImage,
        String author
) {
}