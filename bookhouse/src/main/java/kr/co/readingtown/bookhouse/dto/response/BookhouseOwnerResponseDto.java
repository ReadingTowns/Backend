package kr.co.readingtown.bookhouse.dto.response;

public record BookhouseOwnerResponseDto(
        Long bookhouseId,
        Long memberId,
        String memberName,
        String profileImage,
        Boolean isFollowing,
        Double starRating
) {
}