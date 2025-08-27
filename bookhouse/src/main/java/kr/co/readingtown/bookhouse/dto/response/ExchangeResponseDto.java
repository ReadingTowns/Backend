package kr.co.readingtown.bookhouse.dto.response;

import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;

public record ExchangeResponseDto(
        Long exchangeRequestId,
        RequestStatus status
) {}
