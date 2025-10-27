package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record UserRecommendationResponse(
    Long memberId,
    List<UserRecommendation> recommendations,
    String method
) {}