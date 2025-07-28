package kr.co.readingtown.member.internalapi;

import kr.co.readingtown.member.domain.enums.LoginType;
import kr.co.readingtown.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/members")
public class InternalMemberController {

    private final MemberService memberService;

    @PostMapping
    public void registerMember(
            @RequestParam("provider")LoginType loginType,
            @RequestParam("providerId") String loginId,
            @RequestParam("username") String username) {

        memberService.registerMember(loginType, loginId, username);
    }

    @GetMapping("/id")
    public Long getMemberId(
            @RequestParam("provider")LoginType loginType,
            @RequestParam("providerId") String loginId) {

        return memberService.getMemberId(loginType, loginId);
    }
}
