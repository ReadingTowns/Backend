package kr.co.readingtown.member.externalapi;

import kr.co.readingtown.member.dto.DefaultProfileResponseDto;
import kr.co.readingtown.member.dto.OnboardingRequestDto;
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
                                   @RequestBody OnboardingRequestDto onboardingRequestDto) {

        memberService.completeOnboarding(memberId, onboardingRequestDto);
    }

    @GetMapping("/username/check")
    public Map<String, Boolean> checkUsername(@RequestParam String username) {

        boolean available = memberService.isUsernameAvailable(username);
        return Map.of("isAvailable", available);
    }
}