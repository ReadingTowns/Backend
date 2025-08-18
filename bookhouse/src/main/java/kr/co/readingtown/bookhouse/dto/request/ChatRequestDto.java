package kr.co.readingtown.bookhouse.dto.request;

public record ChatRequestDto(
        Long memberId,
        Long bookId,
        Long chatroomId
) {
}
