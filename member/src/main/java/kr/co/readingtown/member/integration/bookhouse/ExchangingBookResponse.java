package kr.co.readingtown.member.integration.bookhouse;

public record ExchangingBookResponse(
        Long chatRoomId,
        ExchangingBookDetail myBook,
        ExchangingBookDetail yourBook
) {
}