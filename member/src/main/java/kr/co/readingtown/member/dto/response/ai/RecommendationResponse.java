package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record RecommendationResponse(
        List<BookRecommendation> recommendations
) {
}
