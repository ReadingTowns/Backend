package kr.co.readingtown.chat.dto.response;

import kr.co.readingtown.chat.domain.Message;
import kr.co.readingtown.chat.domain.MessageType;

import java.time.LocalDateTime;

public record WebSocketMessageDto(
        Long messageId,
        Long chatroomId,
        Long senderId,
        MessageType messageType,
        String content,
        Long relatedBookhouseId,
        Long relatedExchangeStatusId,
        LocalDateTime sentTime
) {

    public static WebSocketMessageDto of(Message message) {
        return new WebSocketMessageDto(
                message.getMessageId(),
                message.getChatroomId(),
                message.getSenderId(),
                message.getMessageType(),
                message.getContent(),
                message.getRelatedBookhouseId(),
                message.getRelatedExchangeStatusId(),
                message.getSentTime()
        );
    }
}
