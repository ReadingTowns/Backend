package kr.co.readingtown.chat.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageRole {
    USER("user", "사용자 메시지"),
    BOT("bot", "봇 응답");

    private final String value;
    private final String description;
}