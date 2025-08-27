package kr.co.readingtown.bookhouse.externalapi;

import io.swagger.v3.oas.annotations.Operation;
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

    @PostMapping
    @Operation(summary = "교환 요청 생성", description = "채팅방에서 상대방의 책에 대한 교환 요청을 생성합니다.")
    public ExchangeResponseDto createExchangeStatus(@AuthenticationPrincipal Long memberId,
                                      @RequestBody @Valid ExchangeRequestDto exchangeRequestDto) {
        return exchangeStatusService.createExchangeStatus(memberId, exchangeRequestDto);
    }

    @PatchMapping("/{exchangeStatusId}/accept")
    @Operation(summary = "교환 요청 수락", description = "상대방이 보낸 교환 요청을 수락합니다. 양측 모두 수락 시 책 상태가 RESERVED로 변경됩니다.")
    public AcceptExchangeResponseDto acceptExchangeStatus(@AuthenticationPrincipal Long memberId, @PathVariable Long exchangeStatusId) {
        return exchangeStatusService.acceptExchangeStatus(memberId, exchangeStatusId);
    }

    @PatchMapping("/{exchangeStatusId}/reject")
    @Operation(summary = "교환 요청 거절", description = "상대방이 보낸 교환 요청을 거절합니다.")
    public Map<String, String> reject(@AuthenticationPrincipal Long memberId, @PathVariable Long exchangeStatusId) {
        RequestStatus requestStatus = exchangeStatusService.rejectExchangeStatus(memberId, exchangeStatusId);
        return Map.of("requestStatus", requestStatus.name());
    }

    @DeleteMapping("/{exchangeStatusId}/cancel")
    @Operation(summary = "교환 요청 취소", description = "내가 보낸 교환 요청을 취소합니다.")
    public void cancel(@AuthenticationPrincipal Long memberId, @PathVariable Long exchangeStatusId) {
        exchangeStatusService.cancelExchangeStatus(memberId, exchangeStatusId);
    }
}
