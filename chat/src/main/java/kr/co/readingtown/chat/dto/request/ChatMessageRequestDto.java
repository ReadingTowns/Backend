package kr.co.readingtown.chat.dto.request;

import kr.co.readingtown.chat.domain.MessageType;

public record ChatMessageRequestDto(
        Long chatroomId,             // 채팅방 ID
        Long senderId,             // 송신자 ID
        String content,              // 메시지 내용
        MessageType messageType,      // TEXT, EXCHANGE_REQUEST 등
        Long relatedBookhouseId,      // 관련 책 ID
        Long relatedExchangeStatusId // 관련 교환 상태 ID
) {
}
