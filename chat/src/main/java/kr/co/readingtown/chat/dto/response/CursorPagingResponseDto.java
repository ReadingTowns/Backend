package kr.co.readingtown.chat.dto.response;

public record CursorPagingResponseDto(
        Long nextCursor,
        boolean hasMore
) {
}
