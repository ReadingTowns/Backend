package kr.co.readingtown.bookhouse.dto.response;

public record ExchangingBookResponseDto(
        Long bookId,
        String bookImage,
        String bookName,
        String author
) {
}
