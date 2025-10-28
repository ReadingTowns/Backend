package kr.co.readingtown.member.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.co.readingtown.member.dto.request.OnboardingRequestDto;
import kr.co.readingtown.member.dto.request.StarRatingRequestDto;
import kr.co.readingtown.member.dto.request.UpdateProfileRequestDto;
import kr.co.readingtown.member.dto.request.UpdateTownRequestDto;
import kr.co.readingtown.member.dto.response.*;
import kr.co.readingtown.member.integration.bookhouse.BookhouseReader;
import kr.co.readingtown.member.dto.response.internal.ExchangingBookResponseDto;
import kr.co.readingtown.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 관련 API")
public class ExternalMemberController {

    private final MemberService memberService;
    private final BookhouseReader bookhouseReader;

    @GetMapping("/onboarding/default-profile")
    @Operation(summary = "기본 프로필 조회", description = "온보딩 시 사용할 기본 프로필 정보를 조회합니다.")
    public DefaultProfileResponseDto getDefaultProfile(@AuthenticationPrincipal Long memberId) {

        return memberService.getDefaultProfile(memberId);
    }

    @GetMapping("/onboarding/check")
    @Operation(summary = "온보딩 완료 여부 확인", description = "회원의 온보딩 완료 여부를 확인합니다.")
    public Map<String, Boolean> isOnboardingCompleted(@AuthenticationPrincipal Long memberId) {
        Boolean isOnboarded = memberService.isOnboardingCompleted(memberId);
        return Map.of("isOnboarded", isOnboarded);
    }

    @PostMapping("/onboarding/complete")
    @Operation(summary = "온보딩 완료", description = "온보딩 정보를 저장하고 완료 처리합니다.")
    public void completeOnboarding(@AuthenticationPrincipal Long memberId,
                                   @Valid @RequestBody OnboardingRequestDto onboardingRequestDto) {

        memberService.completeOnboarding(memberId, onboardingRequestDto);
    }

    @GetMapping("/nickname/validate")
    @Operation(summary = "닉네임 중복 체크", description = "사용하려는 닉네임이 이미 사용 중인지 확인합니다.")
    public Map<String, Boolean> checkNickname(@AuthenticationPrincipal Long memberId, @RequestParam String nickname) {

        boolean available = memberService.isNicknameAvailable(memberId, nickname);
        return Map.of("isAvailable", available);
    }

    @GetMapping("/town")
    @Operation(summary = "동네 조회", description = "현재 설정된 동네 정보를 조회합니다.")
    public Map<String, String> getTown(@AuthenticationPrincipal Long memberId) {
        String currentTown = memberService.getTown(memberId);
        return Map.of("currentTown",currentTown);
    }
    
    @GetMapping("/town/coordinates")
    @Operation(summary = "위경도로 동네 조회", description = "위경도 좌표로 동네 정보를 조회합니다.")
    public Map<String, String> getTownByCoordinates(@RequestParam BigDecimal longitude,
                                                    @RequestParam BigDecimal latitude) {
        String currentTown = memberService.getTownByCoordinates(longitude, latitude);
        return Map.of("currentTown", currentTown);
    }


    @PutMapping("/town")
    @Operation(summary = "동네 수정", description = "동네 정보를 수정합니다.")
    public Map<String, String> updateTown(@AuthenticationPrincipal Long memberId,
                                      @Valid @RequestBody UpdateTownRequestDto updateTownRequestDto) {
        String currentTown = memberService.updateTown(memberId, updateTownRequestDto);
        return Map.of("currentTown",currentTown);

    }

    @PatchMapping("/profile")
    @Operation(summary = "프로필 수정", description = "회원의 프로필 정보를 수정합니다.")
    public void updateProfile(@AuthenticationPrincipal Long memberId,
                              @Valid @RequestBody UpdateProfileRequestDto updateProfileRequestDto) {
        memberService.updateProfile(memberId, updateProfileRequestDto);
    }

    @GetMapping("/{partnerId}/profile")
    @Operation(summary = "다른 유저의 프로필 조회", description = "특정 회원의 프로필 정보를 조회합니다.")
    public PartnerProfileResponseDto getOtherProfile(@AuthenticationPrincipal Long memberId,
                                                     @PathVariable Long partnerId) {
        return memberService.getPartnerProfile(memberId, partnerId);
    }

    @GetMapping("/me/profile")
    @Operation(summary = "내 프로필 조회", description = "로그인한 회원의 프로필 정보를 조회합니다.")
    public ProfileResponseDto getMyProfile(@AuthenticationPrincipal Long memberId) {
        return memberService.getProfile(memberId);
    }

    @PostMapping("/star-rating")
    @Operation(summary = "유저에 대한 리뷰 별점 제출", description = "교환 후 상대방에 대한 별점을 제출합니다.")
    public Boolean submitStarRating(@AuthenticationPrincipal Long fromMemberId,
                                    @Valid @RequestBody StarRatingRequestDto starRatingRequestDto) {
        return memberService.saveStarRating(fromMemberId, starRatingRequestDto);
    }

    @GetMapping("/{partnerId}/star-rating")
    @Operation(summary = "유저에 대한 리뷰 별점 조회", description = "특정 회원의 별점 정보를 조회합니다.")
    public StarRatingResponseDto getStarRating(@AuthenticationPrincipal Long memberId,
                                               @PathVariable Long partnerId) {
        return memberService.getStarRatingOfPartner(memberId, partnerId);
    }

    @GetMapping("/me/star-rating")
    @Operation(summary = "내 리뷰 별점 조회", description = "로그인한 회원의 별점 정보를 조회합니다.")
    public StarRatingResponseDto getMyStarRating(@AuthenticationPrincipal Long memberId) {
        return memberService.getStarRatingOf(memberId);
    }

    @GetMapping("/search")
    @Operation(summary = "유저 검색", description = "닉네임으로 회원을 검색합니다. 부분 일치를 지원합니다.")
    public List<MemberSearchResponseDto> searchMembers(@AuthenticationPrincipal Long loginMemberId,
                                                       @RequestParam @NotBlank String nickname) {
        return memberService.searchByNickname(loginMemberId, nickname);
    }

    @PostMapping("/{targetMemberId}/follow")
    @Operation(summary = "팔로우", description = "특정 회원을 팔로우합니다.")
    public void follow(@AuthenticationPrincipal Long memberId, @PathVariable("targetMemberId") Long targetMemberId) {
        memberService.follow(memberId, targetMemberId);
    }

    @DeleteMapping("/{targetMemberId}/follow")
    @Operation(summary = "팔로우 취소", description = "특정 회원의 팔로우를 취소합니다.")
    public void unfollow(@AuthenticationPrincipal Long memberId,
                         @PathVariable("targetMemberId") Long targetMemberId) {
        memberService.unfollow(memberId, targetMemberId);
    }

    @GetMapping("/me/following")
    @Operation(summary = "팔로잉 목록 조회", description = "내가 팔로우 중인 회원 목록을 조회합니다.")
    public List<FollowingResponseDto> getMyFollowingAlias(@AuthenticationPrincipal Long loginMemberId) {
        return memberService.getMyFollowing(loginMemberId);
    }

    @GetMapping("/me/followers")
    @Operation(summary = "팔로워 목록 조회", description = "나를 팔로우하는 회원 목록을 조회합니다.")
    public List<FollowerResponseDto> getMyFollowers(@AuthenticationPrincipal Long loginMemberId) {
        return memberService.getMyFollowers(loginMemberId);
    }
    
    @GetMapping("/me/exchanges")
    @Operation(summary = "교환 중인 책 리스트 조회", description = "로그인한 회원이 현재 교환 중인 도서 정보를 조회합니다. 교환한 나의 책과 파트너의 책 정보를 모두 반환합니다.")
    public List<ExchangingBookResponseDto> getMyExchangingBooks(@AuthenticationPrincipal Long memberId) {
        return bookhouseReader.getExchangingBooks(memberId);
    }
}