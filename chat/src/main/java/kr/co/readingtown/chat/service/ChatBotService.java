package kr.co.readingtown.chat.service;

import kr.co.readingtown.chat.config.ChatBotPrompt;
import kr.co.readingtown.chat.domain.ChatBotMessage;
import kr.co.readingtown.chat.domain.MessageRole;
import kr.co.readingtown.chat.dto.*;
import kr.co.readingtown.chat.repository.ChatBotMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatBotService {
    
    private final ChatBotMessageRepository chatBotMessageRepository;
    private final WebClient.Builder webClientBuilder;
    private final ChatBotPrompt chatBotPrompt;
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    @Value("${openai.api.url}")
    private String openaiApiUrl;
    
    @Value("${openai.model:gpt-4o-mini}")
    private String model;
    
    @Transactional
    public ChatBotResponse chat(Long memberId, ChatBotRequest request) {
        // 사용자 메시지 저장
        ChatBotMessage userMessage = ChatBotMessage.builder()
                .memberId(memberId)
                .role(MessageRole.USER)
                .content(request.getMessage())
                .build();
        chatBotMessageRepository.save(userMessage);
        
        // 이전 대화 내역 조회 (최근 10개)
        List<ChatBotMessage> history = chatBotMessageRepository.findTop20ByMemberIdOrderByCreatedAtDesc(memberId);
        
        // OpenAI API 호출
        String botResponse = callOpenAI(history, request.getMessage());
        
        // 봇 응답 저장
        ChatBotMessage botMessage = ChatBotMessage.builder()
                .memberId(memberId)
                .role(MessageRole.BOT)
                .content(botResponse)
                .build();
        chatBotMessageRepository.save(botMessage);
        
        return ChatBotResponse.from(botMessage);
    }
    
    private String callOpenAI(List<ChatBotMessage> history, String currentMessage) {
        WebClient webClient = webClientBuilder
                .baseUrl(openaiApiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openaiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        
        // 메시지 히스토리 구성
        List<OpenAIRequest.Message> messages = new ArrayList<>();
        
        // 시스템 메시지
        messages.add(OpenAIRequest.Message.builder()
                .role("system")
                .content(chatBotPrompt.getPrompt())
                .build());
        
        // 히스토리 추가 (역순으로 정렬되어 있으므로 뒤집어서 추가)
        for (int i = history.size() - 1; i >= 0; i--) {
            ChatBotMessage msg = history.get(i);
            if (!msg.getContent().equals(currentMessage)) {  // 현재 메시지는 제외
                messages.add(OpenAIRequest.Message.builder()
                        .role(msg.getRole() == MessageRole.USER ? "user" : "assistant")
                        .content(msg.getContent())
                        .build());
            }
        }
        
        // 현재 사용자 메시지 추가
        messages.add(OpenAIRequest.Message.builder()
                .role("user")
                .content(currentMessage)
                .build());
        
        OpenAIRequest openAIRequest = OpenAIRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(0.7)
                .build();
        
        try {
            Mono<OpenAIResponse> responseMono = webClient.post()
                    .uri("/v1/chat/completions")
                    .bodyValue(openAIRequest)
                    .retrieve()
                    .bodyToMono(OpenAIResponse.class);
            
            OpenAIResponse response = responseMono.block();
            
            if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
                return response.getChoices().get(0).getMessage().getContent();
            }
            
            return "죄송합니다. 응답을 생성하는데 문제가 발생했습니다.";
            
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생", e);
            return "죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
    
    public List<ChatBotResponse> getChatHistory(Long memberId) {
        List<ChatBotMessage> messages = chatBotMessageRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
        return messages.stream()
                .map(ChatBotResponse::from)
                .toList();
    }
}