package kr.co.readingtown.bookhouse.dto.request;

import jakarta.validation.constraints.NotNull;

public record ExchangeRequestDto(
        @NotNull Long chatRoomId,
        @NotNull Long bookhouseId
) {
}
