package kr.co.readingtown.member.externalapi;

import jakarta.validation.Valid;
import kr.co.readingtown.member.dto.request.OnboardingRequestDto;
import kr.co.readingtown.member.dto.request.StarRatingRequestDto;
import kr.co.readingtown.member.dto.request.UpdateProfileRequestDto;
import kr.co.readingtown.member.dto.request.UpdateTownRequestDto;
import kr.co.readingtown.member.dto.response.DefaultProfileResponseDto;
import kr.co.readingtown.member.dto.response.StarRatingResponseDto;
import kr.co.readingtown.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ExternalMemberController {

    private final MemberService memberService;

    @GetMapping("/onboarding/default-profile")
    public DefaultProfileResponseDto getDefaultProfile(@AuthenticationPrincipal Long memberId) {

        return memberService.getDefaultProfile(memberId);
    }

    //온보딩 저장
    @PostMapping("/onboarding/complete")
    public void completeOnboarding(@AuthenticationPrincipal Long memberId,
                                   @Valid @RequestBody OnboardingRequestDto onboardingRequestDto) {

        memberService.completeOnboarding(memberId, onboardingRequestDto);
    }

    //닉네임 중복 체크
    @GetMapping("/nickname/validate")
    public Map<String, Boolean> checkNickname(@AuthenticationPrincipal Long memberId, @RequestParam String nickname) {

        boolean available = memberService.isNicknameAvailable(memberId, nickname);
        return Map.of("isAvailable", available);
    }

    //동네 조회
    @GetMapping("/town")
    public Map<String, String> getTown(@AuthenticationPrincipal Long memberId) {
        String currentTown = memberService.getTown(memberId);
        return Map.of("",currentTown);
    }

    //동네 수정
    @PutMapping("/town")
    public Map<String, String> updateTown(@AuthenticationPrincipal Long memberId,
                                      @Valid @RequestBody UpdateTownRequestDto updateTownRequestDto) {
        String currentTown = memberService.updateTown(memberId, updateTownRequestDto);
        return Map.of("",currentTown);

    }

    // 프로필 수정
    @PatchMapping("/profile")
    public void updateProfile(@AuthenticationPrincipal Long memberId,
                              @Valid @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
        memberService.updateProfile(memberId, updateProfileRequestDto);
    }

    //유저에 대한 리뷰 별점 제출
    @PostMapping("/star-rating")
    public Boolean submitStarRating(@AuthenticationPrincipal Long fromMemberId,
                                    @Valid @RequestBody StarRatingRequestDto starRatingRequestDto) {
        return memberService.saveStarRating(fromMemberId, starRatingRequestDto);
    }

    //유저에 대한 리뷰 별점 조회
    @GetMapping("/star-rating")
    public StarRatingResponseDto getStarRating(@AuthenticationPrincipal Long loginMemberId,
                                               @RequestParam(value="memberId", required=false) Long memberId) {
        Long targetId = (memberId != null ? memberId : loginMemberId);
        return memberService.getStarRatingOf(targetId);
    }

}