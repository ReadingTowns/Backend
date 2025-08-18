package kr.co.readingtown.bookhouse.externalapi;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import kr.co.readingtown.bookhouse.dto.request.ExchangeRequestDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangeResponseDto;
import kr.co.readingtown.bookhouse.service.ExchangeStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/exchange-requests")
@Tag(name = "ExchangeRequest API", description = "채팅방내 교환 요청 관련 API")
public class ExternalExchangeStatusController {

    private final ExchangeStatusService exchangeStatusService;

    // 교환 요청 생성
    @PostMapping
    public ExchangeResponseDto createExchangeStatus(@AuthenticationPrincipal Long memberId,
                                      @RequestBody @Valid ExchangeRequestDto exchangeRequestDto) {
        return exchangeStatusService.createExchangeStatus(memberId, exchangeRequestDto);
    }

    // 교환 요청 수락
    @PostMapping("/{exchangeRequestId}/accept")
    public Map<String, String> acceptExchangeStatus(@AuthenticationPrincipal Long memberId, @PathVariable Long exchangeRequestId) {
        RequestStatus requestStatus = exchangeStatusService.acceptExchangeStatus(memberId, exchangeRequestId);
        return Map.of("requestStatus", requestStatus.name());
    }

    // 교환 요청 거절
    @PostMapping("/{exchangeRequestId}/reject")
    public Map<String, String> reject(@AuthenticationPrincipal Long memberId,
                       @PathVariable Long exchangeRequestId) {
        RequestStatus requestStatus = exchangeStatusService.rejectExchangeStatus(memberId, exchangeRequestId);
        return Map.of("requestStatus", requestStatus.name());
    }

    // 교환 요청 취소
    @PostMapping("/{exchangeRequestId}/cancel")
    public Map<String, String> cancel(@AuthenticationPrincipal Long memberId,
                                      @PathVariable Long exchangeRequestId) {
        RequestStatus requestStatus = exchangeStatusService.cancelExchangeStatus(memberId, exchangeRequestId);
        return Map.of("requestStatus", requestStatus.name());
    }

}
