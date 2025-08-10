package kr.co.readingtown.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PartnerProfileResponseDto {
    private Long memberId;
    private String profileImage;
    private String nickname;
    private String currentTown;
    private Double userRating;
    private int userRatingCount;
    private String availableTime;
    private Long bookhouseId;
    private boolean isFollowing;
}
