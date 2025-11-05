package kr.co.readingtown.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OpenAIResponse {
    
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Integer index;
        private Message message;
        private String finish_reason;
    }
    
    @Getter
    @NoArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
    
    @Getter
    @NoArgsConstructor
    public static class Usage {
        private Integer prompt_tokens;
        private Integer completion_tokens;
        private Integer total_tokens;
    }
}