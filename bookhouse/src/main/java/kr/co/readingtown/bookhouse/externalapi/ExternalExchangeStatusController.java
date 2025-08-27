package kr.co.readingtown.bookhouse.externalapi;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import kr.co.readingtown.bookhouse.dto.request.ExchangeRequestDto;
import kr.co.readingtown.bookhouse.dto.response.AcceptExchangeResponseDto;
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
    @PatchMapping("/{exchangeStatusId}/accept")
    public AcceptExchangeResponseDto acceptExchangeStatus(@AuthenticationPrincipal Long memberId, @PathVariable Long exchangeStatusId) {
        return exchangeStatusService.acceptExchangeStatus(memberId, exchangeStatusId);
    }

    // 교환 요청 거절
    @PatchMapping("/{exchangeStatusId}/reject")
    public Map<String, String> reject(@AuthenticationPrincipal Long memberId, @PathVariable Long exchangeStatusId) {
        RequestStatus requestStatus = exchangeStatusService.rejectExchangeStatus(memberId, exchangeStatusId);
        return Map.of("requestStatus", requestStatus.name());
    }

    // 교환 요청 취소
    @DeleteMapping("/{exchangeStatusId}/cancel")
    public void cancel(@AuthenticationPrincipal Long memberId, @PathVariable Long exchangeStatusId) {
        exchangeStatusService.cancelExchangeStatus(memberId, exchangeStatusId);
    }
}
