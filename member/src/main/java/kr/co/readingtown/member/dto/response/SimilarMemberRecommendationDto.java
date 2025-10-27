package kr.co.readingtown.member.dto.response;

import kr.co.readingtown.member.domain.Member;
import lombok.Builder;

import java.util.List;

@Builder
public record SimilarMemberRecommendationDto(
    Long memberId,
    String nickname,
    String profileImage,
    String currentTown,
    Double userRating,
    Double similarity,
    List<String> matchedKeywords,
    List<String> matchedBooks,
    String availableTime
) {
    public static SimilarMemberRecommendationDto from(
            Member member, 
            Double similarity, 
            List<String> matchedKeywords, 
            List<String> matchedBooks
    ) {
        return SimilarMemberRecommendationDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .currentTown(member.getCurrentTown())
                .userRating(member.getUserRating())
                .similarity(Math.round((similarity != null ? similarity : 0.0) * 100.0) / 100.0)                .matchedKeywords(matchedKeywords)
                .matchedBooks(matchedBooks)
                .availableTime(member.getAvailableTime())
                .build();
    }
}