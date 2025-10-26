package kr.co.readingtown.member.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.readingtown.member.dto.response.LocalMemberRecommendationDto;
import kr.co.readingtown.member.dto.response.ai.BookRecommendationResponseDto;
import kr.co.readingtown.member.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
@Tag(name = "Member Recommendation API", description = "회원 추천 관련 API")
public class ExternalRecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/recommendations/books")
    @Operation(summary = "추천 도서 조회", description = "회원의 서재 책 기반으로 추천된 도서를 조회합니다.")
    public List<BookRecommendationResponseDto> recommendBooks(@AuthenticationPrincipal Long memberId) {

        return recommendationService.recommendBooks(memberId);
    }

    @GetMapping("/recommendations/local-members")
    @Operation(summary = "동네 기반 추천 유저 조회", description = "위치 기반으로 가까운 유저 10명을 추천합니다.")
    public List<LocalMemberRecommendationDto> getLocalMemberRecommendations(@AuthenticationPrincipal Long memberId) {
        return recommendationService.getLocalMemberRecommendations(memberId);
    }

    @GetMapping("/recommendations/similar-members")
    @Operation(summary = "취향 기반 추천 유저 조회", description = "비슷한 취향을 가진 유저 10명을 추천합니다.")
    public void getSimilarMemberRecommendations(@AuthenticationPrincipal Long memberId) {
        // TODO: 구현 예정
    }
}
