package kr.co.readingtown.bookhouse.dto.request.internal;

public record ExchangeRequestMessageDto(
        Long chatroomId,
        String message,
        String messageType,  // MessageType as String to avoid dependency
        Long exchangeStatusId
) {
}
