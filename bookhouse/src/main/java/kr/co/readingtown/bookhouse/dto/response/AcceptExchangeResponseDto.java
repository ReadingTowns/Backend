package kr.co.readingtown.bookhouse.dto.response;

import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;

public record AcceptExchangeResponseDto(
        RequestStatus requestStatus,
        Boolean isReserved
) {
}
