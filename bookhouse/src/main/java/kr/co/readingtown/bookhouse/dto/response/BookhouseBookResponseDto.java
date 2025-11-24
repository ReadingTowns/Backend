package kr.co.readingtown.bookhouse.dto.response;

public record BookhouseBookResponseDto(
        Long bookhouseId,
        Long bookId,
        String bookImage,
        String bookName,
        String author
) {
}
