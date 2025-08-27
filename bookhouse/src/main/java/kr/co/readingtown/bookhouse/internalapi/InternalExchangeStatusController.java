package kr.co.readingtown.bookhouse.internalapi;

import kr.co.readingtown.bookhouse.dto.request.ChatRequestDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangedBookResponse;
import kr.co.readingtown.bookhouse.dto.response.ExchangingBookResponse;
import kr.co.readingtown.bookhouse.service.BookhouseService;
import kr.co.readingtown.bookhouse.service.ExchangeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InternalExchangeStatusController {

    private final ExchangeStatusService exchangeStatusService;
    private final BookhouseService bookhouseService;

    @GetMapping("/internal/exchange-status/{chatroomId}/book")
    public ExchangedBookResponse getBookIdByChatroomId(
            @PathVariable Long chatroomId,
            @AuthenticationPrincipal Long myId) {

        return exchangeStatusService.getBookIdByChatroomId(chatroomId, myId);
    }

    @PatchMapping("/internal/chatrooms/{chatroomId}/books/complete-exchange")
    public void completeExchange(@PathVariable Long chatroomId) {

        exchangeStatusService.completeExchange(chatroomId);
    }

    @PatchMapping("/internal/chatrooms/{chatroomId}/books/return-exchange")
    public void returnExchange(@PathVariable Long chatroomId) {

        exchangeStatusService.returnExchange(chatroomId);
    }

    @GetMapping("/internal/members/{memberId}/exchanges")
    public List<ExchangingBookResponse> getExchangingBooks(@PathVariable Long memberId) {
        return bookhouseService.getMyExchangingBooks(memberId);
    }
}
