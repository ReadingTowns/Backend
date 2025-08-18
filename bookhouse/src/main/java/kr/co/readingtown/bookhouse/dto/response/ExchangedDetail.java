package kr.co.readingtown.bookhouse.dto.response;

public record ExchangedDetail(
        Long exchangeStatusId,
        Long bookhouseId,
        Long bookId,
        String isAccepted
) {
}
