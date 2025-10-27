package kr.co.readingtown.member.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.readingtown.member.dto.request.KeywordRequest;
import kr.co.readingtown.member.dto.response.KeywordDetailResponse;
import kr.co.readingtown.member.dto.response.KeywordResponse;
import kr.co.readingtown.member.dto.response.YoutubeSearchResponse;
import kr.co.readingtown.member.service.KeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/keyword")
@Tag(name = "Keyword API", description = "키워드 관련 API")
public class ExternalKeywordController {

    private final KeywordService keywordService;

    @GetMapping
    @Operation(summary = "키워드 후보지 조회", description = "선택할 키워드 후보지를 조회합니다.")
    public KeywordResponse getKeyword() {

        return keywordService.getKeyword();
    }

    @PostMapping("/member")
    @Operation(summary = "사용자 키워드 선택", description = "사용자가 키워드를 선택합니다.")
    public void saveMemberKeyword(
            @AuthenticationPrincipal Long memberId,
            @RequestBody KeywordRequest keywordRequest) {

        keywordService.saveKeyword(memberId, keywordRequest);
    }

    @GetMapping("/member")
    @Operation(summary = "사용자의 키워드 조회", description = "사용자가 선택한 키워드를 조회합니다.")
    public List<KeywordDetailResponse> getMemberKeywords(
            @AuthenticationPrincipal Long memberId) {

        return keywordService.getMemberKeywords(memberId);
    }
}
