package kr.co.readingtown.member.externalapi;

import kr.co.readingtown.member.dto.OnboardingRequestDto;
import kr.co.readingtown.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExternalMemberController {

    private final MemberService memberService;

    @PostMapping("/v1/onboarding/complete")
    public Void completeOnboarding(@AuthenticationPrincipal Long memberId,
                                   @RequestBody OnboardingRequestDto onboardingRequestDto) {

        memberService.completeOnboarding(
                memberId,
                onboardingRequestDto.getPhoneNumber(),
                onboardingRequestDto.getCurrentTown(),
                onboardingRequestDto.getUsername(),
                onboardingRequestDto.getProfileImage(),
                onboardingRequestDto.getAvailableTime()
        );
        return null;
    }
}