package kr.co.readingtown.member.client;

import kr.co.readingtown.member.dto.response.ai.RecommendationResponse;
import kr.co.readingtown.member.dto.response.ai.UserRecommendationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "ai-recommend",
        url = "${ai.server-uri}"
)
public interface AiRecommendClient {

    @GetMapping("/recommend")
    RecommendationResponse recommend(
            @RequestParam("book_ids") String bookIds,
            @RequestParam(value = "user_keywords", required = false) String userKeywords
    );
    
    @GetMapping("/recommend/users/keywords")
    UserRecommendationResponse recommendUsersByKeywords(
            @RequestParam("member_id") Long memberId,
            @RequestParam(value = "top_k", defaultValue = "10") Integer topK
    );
    
    @GetMapping("/recommend/users/combined")
    UserRecommendationResponse recommendUsersCombined(
            @RequestParam("member_id") Long memberId,
            @RequestParam(value = "top_k", defaultValue = "10") Integer topK
    );
}
