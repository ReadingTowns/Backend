package kr.co.readingtown.chat.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OpenAIRequest {
    
    private String model;
    private List<Message> messages;
    private Double temperature;
    
    @Getter
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}