package kr.co.readingtown.bookhouse.dto.request.internal;

public record ExchangeRequestMessageDto(
        Long chatroomId,
        Long senderId,  // 메시지 발신자 ID (교환 요청자)
        String message,
        String messageType,  // MessageType as String to avoid dependency
        Long exchangeStatusId
) {
}
