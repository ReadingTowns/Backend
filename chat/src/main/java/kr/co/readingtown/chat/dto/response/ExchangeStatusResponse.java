package kr.co.readingtown.chat.dto.response;

public record ExchangeStatusResponse(
        String status  // RESERVED, EXCHANGED, PENDING 등
) {
}