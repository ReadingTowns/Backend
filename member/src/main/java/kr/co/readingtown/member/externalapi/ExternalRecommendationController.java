package kr.co.readingtown.member.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.readingtown.member.domain.enums.KeywordType;
import kr.co.readingtown.member.dto.request.KeywordRequest;
import kr.co.readingtown.member.dto.response.*;
import kr.co.readingtown.member.dto.response.ai.BookRecommendationResponseDto;
import kr.co.readingtown.member.dto.response.ai.BertSearchResponseDto;
import kr.co.readingtown.member.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recommendations")
@Tag(name = "Recommendation API", description = "회원 추천 관련 API")
public class ExternalRecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/books")
    @Operation(summary = "추천 도서 조회", description = "회원의 서재 책 기반으로 추천된 도서를 조회합니다.")
    public List<BookRecommendationResponseDto> recommendBooks(@AuthenticationPrincipal Long memberId) {

        return recommendationService.recommendBooks(memberId);
    }

    @GetMapping("/local-members")
    @Operation(summary = "동네 기반 추천 유저 조회", description = "위치 기반으로 가까운 유저 10명을 추천합니다.")
    public List<LocalMemberRecommendationDto> getLocalMemberRecommendations(@AuthenticationPrincipal Long memberId) {
        return recommendationService.recommendLocalMembers(memberId);
    }

    @GetMapping("/similar-members")
    @Operation(summary = "취향 기반 추천 유저 조회", description = "비슷한 취향을 가진 유저 10명을 추천합니다.")
    public List<SimilarMemberRecommendationDto> getSimilarMemberRecommendations(@AuthenticationPrincipal Long memberId) {
        return recommendationService.recommendSimilarMembers(memberId);
    }

    @GetMapping("/video")
    @Operation(summary = "키워드 기반 유튜브 영상 조회", description = "사용자가 입력한 키워드 기반으로 유튜브 영상 10개를 반환해줍니다.")
    public List<YoutubeSearchResponse> searchVideo(@RequestParam(name = "keyword") String keyword) {

        return recommendationService.searchVideo(keyword);
    }

    @GetMapping("/books/search")
    @Operation(summary = "키워드 기반 추천 책 조회", description = "사용자가 입력한 키워드 기반으로 추천 책 10권을 반환해줍니다.")
    public BertSearchResponseDto searchBooks(@RequestParam(name = "keyword") String keyword) {

        return recommendationService.recommendBooksByKeyword(keyword);
    }


    @GetMapping("/members/keywords")
    @Operation(summary = "키워드 후보지 조회", description = "선택할 키워드 후보지를 조회합니다.")
    public KeywordResponse getKeyword() {

        return recommendationService.getKeyword();
    }

    @PutMapping("/members/keywords")
    @Operation(summary = "사용자 키워드 수정", description = "사용자가 키워드를 수정합니다.")
    public void updateMemberKeyword(
            @AuthenticationPrincipal Long memberId,
            @RequestBody KeywordRequest keywordRequest) {

        recommendationService.updateKeyword(memberId, keywordRequest);
    }

    @GetMapping("/members/me/keywords")
    @Operation(summary = "사용자의 키워드 조회", description = "사용자가 선택한 키워드를 조회합니다.")
    public List<KeywordDetailResponse> getMemberKeywords(
            @AuthenticationPrincipal Long memberId) {

        return recommendationService.getMemberKeywords(memberId);
    }
}
