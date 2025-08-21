package kr.co.readingtown.chat.dto.response;

import kr.co.readingtown.chat.domain.Message;

import java.time.LocalDateTime;

public record MessageResponseDto(
        Long messageId,
        Long senderId,
        String messageText,
        LocalDateTime sentTime
) {

    public static MessageResponseDto of(Message message) {

        return new MessageResponseDto(
                message.getMessageId(),
                message.getSenderId(),
                message.getContent(),
                message.getSentTime()
        );
    }
}
