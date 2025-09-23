package kr.co.readingtown.chat.dto.response;

import kr.co.readingtown.chat.domain.Message;

import java.time.LocalDateTime;

public record ChatroomPreviewResponseDto(
        Long chatroomId,
        String partnerName,
        String lastMessage,
        LocalDateTime lastMessageTime
) {

    public static ChatroomPreviewResponseDto of(Long chatroomId, String partnerName, Message message) {

        return new ChatroomPreviewResponseDto(
                chatroomId,
                partnerName,
                message != null ? message.getContent() : null,
                message != null ? message.getSentTime() : null
        );
    }
}
