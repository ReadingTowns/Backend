package kr.co.readingtown.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatBotRequest {
    
    private String message;
    
    public ChatBotRequest(String message) {
        this.message = message;
    }
}