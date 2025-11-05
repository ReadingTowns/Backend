package kr.co.readingtown.bookhouse.integration.chat;

import kr.co.readingtown.bookhouse.dto.request.internal.ExchangeRequestMessageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "exchange-status-chat-client",
        url = "${server.base-uri}"
)
public interface ChatClient {

    @PostMapping("/internal/chat/send")
    void sendSystemMessage(@RequestBody ExchangeRequestMessageDto exchangeRequestMessageDto);

}
