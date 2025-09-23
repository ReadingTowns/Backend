package kr.co.readingtown.chat.integration.bookhouse;

import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.dto.response.ExchangedBookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "chat-bookhouse-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {

    @GetMapping("/internal/exchange-status/{chatroomId}/book")
    ExchangedBookResponse getBookIdByChatroomId(@PathVariable Long chatroomId);

    @PatchMapping("/internal/chatrooms/{chatroomId}/books/complete-exchange")
    void completeExchange(@PathVariable("chatroomId") Long chatroomId);

    @PatchMapping("/internal/chatrooms/{chatroomId}/books/return-exchange")
    void returnExchange(@PathVariable("chatroomId") Long chatroomId);
}
