package kr.co.readingtown.chat.dto.request;

public record ChatRequestDto(
        Long memberId,
        Long bookId
) {
}
