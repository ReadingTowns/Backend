package kr.co.readingtown.chat.service;

import kr.co.readingtown.chat.domain.ChatBotMessage;
import kr.co.readingtown.chat.domain.MessageRole;
import kr.co.readingtown.chat.dto.response.ChatBotResponse;
import kr.co.readingtown.chat.repository.ChatBotMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBotMessageService {

    private final ChatBotMessageRepository chatBotMessageRepository;

    @Transactional(timeout = 5)
    public void saveUserMessage(Long memberId, String content) {
        ChatBotMessage userMessage = ChatBotMessage.builder()
                .memberId(memberId)
                .role(MessageRole.USER)
                .content(content)
                .build();
        chatBotMessageRepository.save(userMessage);
        log.debug("사용자 메시지 저장 완료: memberId={}", memberId);
    }

    @Transactional(readOnly = true, timeout = 5)
    public List<ChatBotMessage> loadHistory(Long memberId) {
        List<ChatBotMessage> history = chatBotMessageRepository.findTop20ByMemberIdOrderByCreatedAtDesc(memberId);
        log.debug("대화 내역 조회 완료: memberId={}, historySize={}", memberId, history.size());
        return history;
    }

    @Transactional(timeout = 5)
    public ChatBotResponse saveBotResponse(Long memberId, String content) {
        ChatBotMessage botMessage = ChatBotMessage.builder()
                .memberId(memberId)
                .role(MessageRole.BOT)
                .content(content)
                .build();
        chatBotMessageRepository.save(botMessage);
        log.debug("봇 응답 저장 완료: memberId={}", memberId);
        return ChatBotResponse.from(botMessage);
    }
}
