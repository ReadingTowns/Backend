package kr.co.readingtown.bookhouse.dto.response;

public record ExchangingBookResponse(
        Long chatroomId,
        ExchangingBookDetail myBook,
        ExchangingBookDetail partnerBook
) {
}