package kr.co.readingtown.bookhouse.dto.response;

public record MemberProfileResponseDto(
        String nickname,
        String profileImage,
        Double starRating
) {
}
