package kr.co.readingtown.member.dto.response;

import kr.co.readingtown.member.domain.Member;
import lombok.Builder;

@Builder
public record LocalMemberRecommendationDto(
    Long memberId,
    String nickname,
    String profileImage,
    String currentTown,
    Double userRating,
    String distance,
    String availableTime
) {
    public static LocalMemberRecommendationDto from(Member member, double distanceKm) {
        String distanceText = formatDistance(distanceKm);
        
        return LocalMemberRecommendationDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .currentTown(member.getCurrentTown())
                .userRating(member.getUserRating())
                .distance(distanceText)
                .availableTime(member.getAvailableTime())
                .build();
    }
    
    private static String formatDistance(double distanceKm) {
        if (distanceKm < 1.0) {
            return String.format("%.0fm", distanceKm * 1000);
        } else if (distanceKm < 10.0) {
            return String.format("%.1fkm", distanceKm);
        } else {
            return String.format("%.0fkm", distanceKm);
        }
    }
}