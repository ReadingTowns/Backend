package kr.co.readingtown.bookhouse.internalapi;

import kr.co.readingtown.bookhouse.dto.request.ChatRequestDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangedBookResponse;
import kr.co.readingtown.bookhouse.service.ExchangeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/exchange-status")
public class InternalExchangeStatusController {

    private final ExchangeStatusService exchangeStatusService;

    @GetMapping("/{chatroomId}/book")
    public ExchangedBookResponse getBookIdByChatroomId(
            @PathVariable Long chatroomId,
            @AuthenticationPrincipal Long myId) {

        return exchangeStatusService.getBookIdByChatroomId(chatroomId, myId);
    }
}
