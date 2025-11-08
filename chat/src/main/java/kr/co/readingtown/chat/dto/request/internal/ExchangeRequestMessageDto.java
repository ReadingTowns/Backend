package kr.co.readingtown.chat.dto.request.internal;

public record ExchangeRequestMessageDto(
        Long chatroomId,
        Long senderId,  // 메시지 발신자 ID (교환 요청자)
        String message,
        String messageType,  // Receive as String from external service
        Long exchangeStatusId
) {
}