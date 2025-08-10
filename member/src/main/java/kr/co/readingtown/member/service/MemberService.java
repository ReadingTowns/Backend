package kr.co.readingtown.member.service;

import kr.co.readingtown.common.config.AppProperties;
import kr.co.readingtown.common.exception.CustomException;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import kr.co.readingtown.member.dto.request.OnboardingRequestDto;
import kr.co.readingtown.member.dto.request.StarRatingRequestDto;
import kr.co.readingtown.member.dto.request.UpdateProfileRequestDto;
import kr.co.readingtown.member.dto.request.UpdateTownRequestDto;
import kr.co.readingtown.member.dto.response.DefaultProfileResponseDto;
import kr.co.readingtown.member.dto.response.StarRatingResponseDto;
import kr.co.readingtown.member.exception.MemberException;
import kr.co.readingtown.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AppProperties appProperties;
    private final LocationService locationService;

    @Transactional
    public void registerMember(LoginType loginType, String loginId, String username) {

        if (!memberRepository.existsByLoginTypeAndLoginId(loginType, loginId)) {

            Member newMember = Member.builder()
                    .loginType(loginType)
                    .loginId(loginId)
                    .username(username)
                    .nickname(null) //기본 닉네임 Null로 설정 후 온보딩에서 설정
                    .userRatingSum(0)
                    .userRatingCount(0)
                    .build();
            memberRepository.save(newMember);
        }
    }

    public Long getMemberId(LoginType loginType, String loginId) {

        Member member = memberRepository.findByLoginTypeAndLoginId(loginType, loginId);
        return member.getMemberId();
    }

    @Transactional
    public void completeOnboarding(Long memberId, OnboardingRequestDto onboardingRequestDto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        // 카카오 API로 동네명 해석
        String currentTown = locationService.resolveTown(
                onboardingRequestDto.getLongitude(),
                onboardingRequestDto.getLatitude()
        );

        member.completeOnboarding(
                onboardingRequestDto.getPhoneNumber(),
                currentTown,
                onboardingRequestDto.getLongitude(), //경도
                onboardingRequestDto.getLatitude(), //위도
                onboardingRequestDto.getNickname(),
                onboardingRequestDto.getProfileImage(),
                onboardingRequestDto.getAvailableTime()
        );
    }

    public DefaultProfileResponseDto getDefaultProfile(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NoAuthMember::new);

        String defaultNickname = generateUniqueNickname();
        String defaultImageUrl = appProperties.getDefaultProfileImageUrl();

        return new DefaultProfileResponseDto(defaultNickname, defaultImageUrl);
    }

    //랜덤 닉네임 설정
    private String generateUniqueNickname() {

        String prefix = "리딩여우";
        String candidate;
        int attempt = 0;

        //리딩여우+랜덤숫자문자 생성
        do {
            String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            candidate = prefix + random;
            attempt++;
            if (attempt > 10) {
                throw new MemberException.NicknameGenerationFailed();
            }
        } while (memberRepository.existsByNickname(candidate));

        return candidate;
    }

    //닉네임 사용가능 여부 확인
    public boolean isNicknameAvailable(Long memberId, String nickname) {

        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NoAuthMember::new);

        //null 체크
        if (nickname == null || nickname.isBlank()) {
            throw new MemberException.InvalidNickname();
        }

        // 길이 제한 체크
        if (nickname.length() < 2 || nickname.length() > 20) {
            throw new MemberException.InvalidNickname();
        }

        //중복체크
        if (memberRepository.existsByNickname(nickname)) {
            throw new MemberException.UsernameAlreadyExists();
        }

        return true;
    }

    @Transactional
    public Boolean saveStarRating(Long fromMemberId, StarRatingRequestDto starRatingRequestDto) {
        Member member = memberRepository.findById(starRatingRequestDto.getMemberId())
                .orElseThrow(MemberException.NoAuthMember::new);

        // 자기 자신을 평가하는 경우 예외 처리 (선택적)
        if (fromMemberId.equals(starRatingRequestDto.getMemberId())) {
            throw new MemberException.SelfRatingNotAllowed();
        }

        // 평점 추가
        member.addStarRating(starRatingRequestDto.getStarRating());
        memberRepository.save(member);

        return true;
    }

    @Transactional
    public void updateProfile(Long memberId, UpdateProfileRequestDto updateProfileRequestDto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        member.updateProfile(
                updateProfileRequestDto.getNickname(),
                updateProfileRequestDto.getProfileImage(),
                updateProfileRequestDto.getAvailableTime()
        );
    }

    public StarRatingResponseDto getStarRatingOf(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        return StarRatingResponseDto.builder()
                .memberId(member.getMemberId())
                .userRatingSum(member.getUserRatingSum())
                .userRatingCount(member.getUserRatingCount())
                .userRating(member.getUserRating())
                .build();
    }

    public String getTown(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        return member.getCurrentTown();
    }

    public String updateTown(Long memberId, UpdateTownRequestDto updateTownRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        // 범위 검증 (-90~90, -180~180)
        double lat = updateTownRequestDto.getLatitude().doubleValue();
        double lon = updateTownRequestDto.getLongitude().doubleValue();
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new MemberException.TownResolvedFailed();
        }

        // 경위도 -> 동네명 해석 (coord2region → 실패 시 coord2address → 둘 다 실패 시 LocationException.TownResolveFailed)
        String currentTown = locationService.resolveTown(updateTownRequestDto.getLongitude(), updateTownRequestDto.getLatitude());

        member.updateTown(updateTownRequestDto.getLatitude(), updateTownRequestDto.getLongitude(), currentTown);
        return currentTown;
    }
}
