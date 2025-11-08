package kr.co.readingtown.bookhouse.dto.response;

public record ExchangeStatusResponse(
        String status  // PENDING, RESERVED, EXCHANGED
) {
}
