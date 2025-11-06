package kr.co.readingtown.chat.dto.response;

import kr.co.readingtown.chat.domain.ChatBotMessage;
import kr.co.readingtown.chat.domain.MessageRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatBotResponse {
    
    private Long messageId;
    private String message;
    private MessageRole role;
    private LocalDateTime createdAt;
    
    public static ChatBotResponse from(ChatBotMessage chatBotMessage) {
        return ChatBotResponse.builder()
                .messageId(chatBotMessage.getMessageId())
                .message(chatBotMessage.getContent())
                .role(chatBotMessage.getRole())
                .createdAt(chatBotMessage.getCreatedAt())
                .build();
    }
}