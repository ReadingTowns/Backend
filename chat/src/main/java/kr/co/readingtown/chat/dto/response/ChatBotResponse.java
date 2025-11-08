package kr.co.readingtown.chat.dto.response;

import kr.co.readingtown.chat.domain.ChatBotMessage;
import kr.co.readingtown.chat.domain.MessageRole;

import java.time.LocalDateTime;

public record ChatBotResponse(
        Long messageId,
        String message,
        MessageRole role,
        LocalDateTime createdAt,
        Boolean showDate,      // 날짜 라벨 표시 여부
        String dateLabel,      // "2025년 8월 16일 오후 5:46"
        Boolean showTime,      // 시간 라벨 표시 여부
        String timeLabel       // "오후 5:46"
) {
    public static ChatBotResponse from(ChatBotMessage chatBotMessage) {
        return new ChatBotResponse(
                chatBotMessage.getMessageId(),
                chatBotMessage.getContent(),
                chatBotMessage.getRole(),
                chatBotMessage.getCreatedAt(),
                null,  // Service에서 계산
                null,
                null,
                null
        );
    }
}