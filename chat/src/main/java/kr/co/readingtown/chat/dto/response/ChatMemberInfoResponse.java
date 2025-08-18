package kr.co.readingtown.chat.dto.response;

public record ChatMemberInfoResponse(
        Long memberId,
        String nickname,
        String profileImage
) {
}
