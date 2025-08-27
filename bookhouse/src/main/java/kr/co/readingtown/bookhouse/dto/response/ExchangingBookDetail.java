package kr.co.readingtown.bookhouse.dto.response;

public record ExchangingBookDetail(
        Long bookhouseId,
        String bookName,
        String bookImage
) {
}