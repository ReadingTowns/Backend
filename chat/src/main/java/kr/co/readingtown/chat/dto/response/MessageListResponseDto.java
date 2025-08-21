package kr.co.readingtown.chat.dto.response;

import java.util.List;

public record MessageListResponseDto(
        Long myMemberId,
        List<MessageResponseDto> message,
        CursorPagingResponseDto paging
) {
}
