package kr.co.readingtown.chat.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.readingtown.chat.dto.request.ChatBotRequest;
import kr.co.readingtown.chat.dto.response.ChatBotResponse;
import kr.co.readingtown.chat.dto.response.ChatHistoryPageResponse;
import kr.co.readingtown.chat.service.ChatBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chatbot")
@Tag(name = "ChatBot API", description = "챗봇 관련 API")
public class ExternalChatBotController {
    
    private final ChatBotService chatBotService;
    
    @PostMapping("/chat")
    @Operation(summary = "챗봇과 대화", description = "사용자가 챗봇에게 메시지를 보내고 응답을 받습니다.")
    public ChatBotResponse chat(
            @Valid @RequestBody ChatBotRequest request,
            @AuthenticationPrincipal Long memberId) {
        
        return chatBotService.chat(memberId, request);
    }
    
    @GetMapping("/history")
    @Operation(summary = "대화 내역 조회", description = "사용자의 챗봇 대화 내역을 페이지네이션으로 조회합니다.")
    public ChatHistoryPageResponse getChatHistory(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) Long beforeId,
            @RequestParam(defaultValue = "30") int limit) {

        return chatBotService.getChatHistory(memberId, beforeId, limit);
    }
}