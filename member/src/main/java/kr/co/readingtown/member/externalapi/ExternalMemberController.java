package kr.co.readingtown.member.externalapi;

import jakarta.validation.Valid;
import kr.co.readingtown.member.dto.*;
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

    @PostMapping("/onboarding/complete")
    public void completeOnboarding(@AuthenticationPrincipal Long memberId,
                                   @Valid @RequestBody OnboardingRequestDto onboardingRequestDto) {

        memberService.completeOnboarding(memberId, onboardingRequestDto);
    }

    //닉네임 중복 체크
    @GetMapping("/username/check")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {

        boolean available = memberService.isUsernameAvailable(username);
        return Map.of("isAvailable", available);
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
            @RequestParam(value="memberId", required=false) Long memberId
    ) {
        Long targetId = (memberId != null ? memberId : loginMemberId);
        return memberService.getStarRatingOf(targetId);
    }

    // 프로필 수정
    @PatchMapping("/profile")
    public void updateProfile(@AuthenticationPrincipal Long memberId,
                              @Valid @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
        memberService.updateProfile(memberId, updateProfileRequestDto);
    }

}