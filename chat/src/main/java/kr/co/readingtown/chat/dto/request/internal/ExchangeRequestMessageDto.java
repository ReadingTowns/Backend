package kr.co.readingtown.chat.dto.request.internal;

public record ExchangeRequestMessageDto(
        Long chatroomId,
        String message,
        String messageType,  // Receive as String from external service
        Long exchangeStatusId
) {
}