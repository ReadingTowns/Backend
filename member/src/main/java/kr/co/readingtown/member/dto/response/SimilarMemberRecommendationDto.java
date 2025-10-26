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
    String matchReason,
    String availableTime
) {
    public static SimilarMemberRecommendationDto from(
            Member member, 
            Double similarity, 
            List<String> matchedKeywords, 
            List<String> matchedBooks
    ) {
        String matchReason = buildMatchReason(matchedKeywords, matchedBooks);
        
        return SimilarMemberRecommendationDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .profileImage(member.getProfileImage())
                .currentTown(member.getCurrentTown())
                .userRating(member.getUserRating())
                .similarity(Math.round(similarity * 100.0) / 100.0)
                .matchedKeywords(matchedKeywords)
                .matchedBooks(matchedBooks)
                .matchReason(matchReason)
                .availableTime(member.getAvailableTime())
                .build();
    }
    
    private static String buildMatchReason(List<String> keywords, List<String> books) {
        if (!keywords.isEmpty() && !books.isEmpty()) {
            return String.format("%s 취향이 비슷하고 비슷한 책을 보유하고 있어요", String.join(", ", keywords));
        } else if (!keywords.isEmpty()) {
            return String.format("%s 취향이 비슷해요", String.join(", ", keywords));
        } else if (!books.isEmpty()) {
            return "비슷한 책을 보유하고 있어요";
        }
        return "취향이 비슷해요";
    }
}