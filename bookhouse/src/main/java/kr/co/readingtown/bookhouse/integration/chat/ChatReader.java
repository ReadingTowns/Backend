package kr.co.readingtown.bookhouse.integration.chat;

import kr.co.readingtown.bookhouse.dto.request.internal.ExchangeRequestMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatReader {

    private final ChatClient chatClient;

    public void sendSystemMessage(ExchangeRequestMessageDto exchangeRequestMessageDto){
        chatClient.sendSystemMessage(exchangeRequestMessageDto);
    }
}