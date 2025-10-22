package kr.co.readingtown.member.dto.response;

public record MemberProfileResponseDto(
        String nickname,
        String profileImage,
        Double starRating
) {
}