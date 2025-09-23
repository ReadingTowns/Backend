package kr.co.readingtown.chat.dto.response;

public record ExchangedDetail(
        Long exchangeStatusId,
        Long bookhouseId,
        Long bookId,
        String isAccepted
) {
}
