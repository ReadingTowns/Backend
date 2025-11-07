package kr.co.readingtown.chat.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ChatBotRequest(
    @NotBlank(message = "메시지는 비어있을 수 없습니다")
    String message
) {}