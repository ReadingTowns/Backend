package kr.co.readingtown.chat.service;

import kr.co.readingtown.chat.config.ChatBotPrompt;
import kr.co.readingtown.chat.domain.ChatBotMessage;
import kr.co.readingtown.chat.domain.MessageRole;
import kr.co.readingtown.chat.dto.request.ChatBotRequest;
import kr.co.readingtown.chat.dto.request.OpenAIRequest;
import kr.co.readingtown.chat.dto.response.ChatBotResponse;
import kr.co.readingtown.chat.dto.response.ChatHistoryPageResponse;
import kr.co.readingtown.chat.dto.response.OpenAIResponse;
import kr.co.readingtown.chat.exception.ChatException;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotService {

    private final ChatBotMessageRepository chatBotMessageRepository;
    private final ChatBotMessageService chatBotMessageService;
    private final WebClient.Builder webClientBuilder;
    private final ChatBotPrompt chatBotPrompt;
    
    @Value("${openai.api.key}")
    private String openaiApiKey;
    
    @Value("${openai.api.url}")
    private String openaiApiUrl;
    
    @Value("${openai.model:gpt-4o-mini}")
    private String model;
    
    public ChatBotResponse chat(Long memberId, ChatBotRequest request) {
        // Step 1: 사용자 메시지 저장 (독립적인 트랜잭션)
        try {
            chatBotMessageService.saveUserMessage(memberId, request.message());
            log.debug("사용자 메시지 저장 완료: memberId={}, message={}", memberId, request.message());
        } catch (Exception e) {
            log.error("사용자 메시지 저장 실패: memberId={}, message={}", memberId, request.message(), e);
            throw new ChatException.MessageSaveFailed();
        }

        // Step 2: 대화 내역 조회 (읽기 전용 트랜잭션)
        List<ChatBotMessage> history;
        try {
            history = chatBotMessageService.loadHistory(memberId);
            log.debug("대화 내역 조회 완료: memberId={}, historySize={}", memberId, history.size());
        } catch (Exception e) {
            log.error("대화 내역 조회 실패: memberId={}", memberId, e);
            // 히스토리 조회 실패 시 빈 리스트로 진행 (치명적이지 않음)
            history = new ArrayList<>();
        }

        // Step 3: OpenAI API 호출 (트랜잭션 없음 - DB 커넥션 점유 없음)
        String botResponse;
        try {
            botResponse = callOpenAI(history, request.message());
            log.debug("OpenAI 응답 수신 완료: memberId={}", memberId);
        } catch (Exception e) {
            log.error("OpenAI API 호출 실패: memberId={}, message={}", memberId, request.message(), e);
            // API 호출 실패 시 에러 메시지를 봇 응답으로 사용 (사용자에게 피드백 제공)
            botResponse = "죄송합니다. 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }

        // Step 4: 봇 응답 저장 (독립적인 트랜잭션)
        try {
            ChatBotResponse response = chatBotMessageService.saveBotResponse(memberId, botResponse);
            log.debug("봇 응답 저장 완료: memberId={}", memberId);
            return response;
        } catch (Exception e) {
            log.error("봇 응답 저장 실패: memberId={}, response={}", memberId, botResponse, e);
            throw new ChatException.ResponseSaveFailed();
        }
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
                    .bodyToMono(OpenAIResponse.class)
                    .timeout(Duration.ofSeconds(15));

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
    
    public ChatHistoryPageResponse getChatHistory(Long memberId, Long beforeId, int limit) {
        // 최대 100개 제한
        limit = Math.min(limit, 100);

        // 메시지 조회
        List<ChatBotMessage> messages;
        if (beforeId == null) {
            messages = chatBotMessageRepository.findByMemberIdOrderByCreatedAtDesc(
                    memberId,
                    org.springframework.data.domain.PageRequest.of(0, limit)
            );
        } else {
            messages = chatBotMessageRepository.findByMemberIdAndMessageIdLessThanOrderByCreatedAtDesc(
                    memberId,
                    beforeId,
                    org.springframework.data.domain.PageRequest.of(0, limit)
            );
        }

        // UI 필드 계산 (showDate, showTime, labels)
        List<ChatBotResponse> responses = calculateUIFields(messages);

        // hasMore, nextBeforeId 계산
        boolean hasMore = messages.size() == limit;
        Long nextBeforeId = hasMore ? messages.get(messages.size() - 1).getMessageId() : null;

        return new ChatHistoryPageResponse(responses, hasMore, nextBeforeId);
    }

    private List<ChatBotResponse> calculateUIFields(List<ChatBotMessage> messages) {
        if (messages.isEmpty()) {
            return List.of();
        }

        List<ChatBotResponse> responses = new ArrayList<>();
        LocalDateTime previousDateTime = null;

        for (ChatBotMessage message : messages) {
            LocalDateTime currentDateTime = message.getCreatedAt();

            boolean showDate = false;
            String dateLabel = null;
            boolean showTime = false;
            String timeLabel = null;

            if (previousDateTime == null) {
                // 첫 메시지
                showDate = true;
                dateLabel = formatDateLabel(currentDateTime);
            } else {
                // 이전 메시지와 날짜 비교
                if (!isSameDay(previousDateTime, currentDateTime)) {
                    showDate = true;
                    dateLabel = formatDateLabel(currentDateTime);
                } else {
                    // 같은 날이면 시간 차이 확인 (1분 이상)
                    if (java.time.Duration.between(currentDateTime, previousDateTime).toMinutes() >= 1) {
                        showTime = true;
                        timeLabel = formatTimeLabel(currentDateTime);
                    }
                }
            }

            responses.add(new ChatBotResponse(
                    message.getMessageId(),
                    message.getContent(),
                    message.getRole(),
                    message.getCreatedAt(),
                    showDate,
                    dateLabel,
                    showTime,
                    timeLabel
            ));

            previousDateTime = currentDateTime;
        }

        return responses;
    }

    private boolean isSameDay(LocalDateTime dt1, LocalDateTime dt2) {
        return dt1.toLocalDate().equals(dt2.toLocalDate());
    }

    private String formatDateLabel(LocalDateTime dateTime) {
        // "2025년 8월 16일 오후 5:46"
        int hour = dateTime.getHour();
        String amPm = hour < 12 ? "오전" : "오후";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;

        return String.format("%d년 %d월 %d일 %s %d:%02d",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth(),
                amPm,
                displayHour,
                dateTime.getMinute()
        );
    }

    private String formatTimeLabel(LocalDateTime dateTime) {
        // "오후 5:46"
        int hour = dateTime.getHour();
        String amPm = hour < 12 ? "오전" : "오후";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;

        return String.format("%s %d:%02d",
                amPm,
                displayHour,
                dateTime.getMinute()
        );
    }
}