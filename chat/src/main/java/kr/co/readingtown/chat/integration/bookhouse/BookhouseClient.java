package kr.co.readingtown.chat.integration.bookhouse;

import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.dto.response.ExchangedBookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "chat-bookhouse-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {

    @PostMapping("/internal/exchange-status")
    void createExchangeStatus(@RequestBody ChatRequestDto chatRequestDto);

    @GetMapping("/internal/exchange-status/{chatroomId}/book")
    ExchangedBookResponse getBookIdByChatroomId(@PathVariable Long chatroomId);
}
