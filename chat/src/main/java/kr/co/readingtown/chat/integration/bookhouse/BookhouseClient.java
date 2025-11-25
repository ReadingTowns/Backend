package kr.co.readingtown.chat.integration.bookhouse;

import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.dto.response.ExchangedBookResponse;
import kr.co.readingtown.chat.dto.response.ExchangeStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "chat-bookhouse-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {

    @GetMapping("/internal/exchange-status/{chatroomId}/book")
    ExchangedBookResponse getBookIdByChatroomId(@PathVariable Long chatroomId, @RequestParam("myId") Long myId);

    @GetMapping("/internal/chatrooms/{chatroomId}/exchange-status")
    Boolean isExchanging(@PathVariable Long chatroomId);

    @GetMapping("/internal/bookhouse/{chatroomId}/status")
    ExchangeStatusResponse getExchangeStatus(@PathVariable Long chatroomId);

    @PostMapping("/internal/chatrooms/{chatroomId}/books/complete-exchange")
    void completeExchange(@PathVariable("chatroomId") Long chatroomId);

    @PostMapping("/internal/chatrooms/{chatroomId}/books/return-exchange")
    void returnExchange(@PathVariable("chatroomId") Long chatroomId);
}
