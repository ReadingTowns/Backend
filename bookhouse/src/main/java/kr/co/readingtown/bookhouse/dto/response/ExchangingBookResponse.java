package kr.co.readingtown.bookhouse.dto.response;

public record ExchangingBookResponse(
        Long chatRoomId,
        ExchangingBookDetail myBook,
        ExchangingBookDetail yourBook
) {
}