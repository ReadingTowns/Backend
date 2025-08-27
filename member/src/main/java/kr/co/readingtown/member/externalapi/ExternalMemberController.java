package kr.co.readingtown.member.externalapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.co.readingtown.member.dto.request.OnboardingRequestDto;
import kr.co.readingtown.member.dto.request.StarRatingRequestDto;
import kr.co.readingtown.member.dto.request.UpdateProfileRequestDto;
import kr.co.readingtown.member.dto.request.UpdateTownRequestDto;
import kr.co.readingtown.member.dto.response.*;
import kr.co.readingtown.member.integration.bookhouse.BookhouseReader;
import kr.co.readingtown.member.integration.bookhouse.ExchangingBookResponse;
import kr.co.readingtown.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class ExternalMemberController {

    private final MemberService memberService;
    private final BookhouseReader bookhouseReader;

    @GetMapping("/onboarding/default-profile")
    public DefaultProfileResponseDto getDefaultProfile(@AuthenticationPrincipal Long memberId) {

        return memberService.getDefaultProfile(memberId);
    }

    //온보딩 완료 여부 확인
    @GetMapping("/onboarding/check")
    public Map<String, Boolean> isOnboardingCompleted(@AuthenticationPrincipal Long memberId) {
        Boolean isOnboarded = memberService.isOnboardingCompleted(memberId);
        return Map.of("isOnboarded", isOnboarded);
    }

    //온보딩 저장 및 온보딩 완료 여부 true로 수정
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
        return Map.of("currentTown",currentTown);
    }

    //동네 수정
    @PutMapping("/town")
    public Map<String, String> updateTown(@AuthenticationPrincipal Long memberId,
                                      @Valid @RequestBody UpdateTownRequestDto updateTownRequestDto) {
        String currentTown = memberService.updateTown(memberId, updateTownRequestDto);
        return Map.of("currentTown",currentTown);

    }

    // 프로필 수정
    @PatchMapping("/profile")
    public void updateProfile(@AuthenticationPrincipal Long memberId,
                              @Valid @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
        memberService.updateProfile(memberId, updateProfileRequestDto);
    }

    //다른 유저의 프로필 조회
    @GetMapping("/{partnerId}/profile")
    public PartnerProfileResponseDto getOtherProfile(@AuthenticationPrincipal Long memberId,
                                                     @PathVariable Long partnerId) {
        return memberService.getPartnerProfile(memberId, partnerId);
    }

    //내 프로필 조회
    @GetMapping("/me/profile")
    public ProfileResponseDto getMyProfile(@AuthenticationPrincipal Long memberId) {
        return memberService.getProfile(memberId);
    }

    //유저에 대한 리뷰 별점 제출
    @PostMapping("/star-rating")
    public Boolean submitStarRating(@AuthenticationPrincipal Long fromMemberId,
                                    @Valid @RequestBody StarRatingRequestDto starRatingRequestDto) {
        return memberService.saveStarRating(fromMemberId, starRatingRequestDto);
    }

    //유저에 대한 리뷰 별점 조회
    @GetMapping("/{partnerId}/star-rating")
    public StarRatingResponseDto getStarRating(@AuthenticationPrincipal Long memberId,
                                               @PathVariable Long partnerId) {
        return memberService.getStarRatingOfPartner(memberId, partnerId);
    }

    //내 리뷰 별점 조회
    @GetMapping("/me/star-rating")
    public StarRatingResponseDto getMyStarRating(@AuthenticationPrincipal Long memberId) {
        return memberService.getStarRatingOf(memberId);
    }

    // 유저 검색 (부분 일치)
    @GetMapping("/search")
    public List<MemberSearchResponseDto> searchMembers(@AuthenticationPrincipal Long loginMemberId,
                                                       @RequestParam @NotBlank String nickname) {
        return memberService.searchByNickname(loginMemberId, nickname);
    }

    // 팔로우 생성
    @PostMapping("/{targetMemberId}/follow")
    public void follow(@AuthenticationPrincipal Long memberId, @PathVariable("targetMemberId") Long targetMemberId) {
        memberService.follow(memberId, targetMemberId);
    }

    // 팔로우 취소
    @DeleteMapping("/{targetMemberId}/follow")
    public void unfollow(@AuthenticationPrincipal Long memberId,
                         @PathVariable("targetMemberId") Long targetMemberId) {
        memberService.unfollow(memberId, targetMemberId);
    }

    // 내가 팔로우 중인 유저(= following) 리스트 조회
    @GetMapping("/me/following")
    public List<FollowingResponseDto> getMyFollowingAlias(@AuthenticationPrincipal Long loginMemberId) {
        return memberService.getMyFollowing(loginMemberId);
    }

    // 나를 팔로우하는 유저(= followers) 리스트 조회
    @GetMapping("/me/followers")
    public List<FollowerResponseDto> getMyFollowers(@AuthenticationPrincipal Long loginMemberId) {
        return memberService.getMyFollowers(loginMemberId);
    }
    
    // 교환 중인 책 리스트 조회
    @GetMapping("/me/exchanges")
    public List<ExchangingBookResponse> getMyExchangingBooks(@AuthenticationPrincipal Long memberId) {
        return bookhouseReader.getExchangingBooks(memberId);
    }
}