package kr.co.readingtown.member.integration.bookhouse;

public record ExchangingBookDetail(
        Long bookhouseId,
        String bookName,
        String bookImage
) {
}