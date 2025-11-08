package kr.co.readingtown.chat.internalapi;

import kr.co.readingtown.chat.domain.MessageType;
import kr.co.readingtown.chat.dto.request.internal.ExchangeRequestMessageDto;
import kr.co.readingtown.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/chat")
public class InternalChatController {
    
    private final ChatService chatService;
    
    @PostMapping("/send")
    public void sendSystemMessage(@RequestBody ExchangeRequestMessageDto exchangeRequestMessageDto) {
        // String을 MessageType으로 변환 (잘못된 타입은 SYSTEM으로 처리)
        MessageType messageType;
        try {
            messageType = MessageType.valueOf(exchangeRequestMessageDto.messageType());
        } catch (IllegalArgumentException | NullPointerException e) {
            // 매핑되지 않는 타입이거나 null인 경우 SYSTEM으로 기본 처리
            messageType = MessageType.SYSTEM;
        }
        
        // ChatService의 sendSystemMessage 호출
        chatService.sendSystemMessage(
                exchangeRequestMessageDto.chatroomId(),
                exchangeRequestMessageDto.senderId(),
                exchangeRequestMessageDto.message(),
                messageType,
                exchangeRequestMessageDto.exchangeStatusId()
        );
    }
}
