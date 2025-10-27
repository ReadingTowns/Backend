package kr.co.readingtown.member.service;

import kr.co.readingtown.common.config.AppProperties;
import kr.co.readingtown.member.dto.request.internal.FollowBulkCheckRequestDto;
import kr.co.readingtown.member.integration.bookhouse.FollowClient;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;

import kr.co.readingtown.member.dto.query.MemberIdNameDto;
import kr.co.readingtown.member.dto.request.*;
import kr.co.readingtown.member.dto.response.*;
import kr.co.readingtown.member.exception.MemberException;
import kr.co.readingtown.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AppProperties appProperties;
    private final LocationService locationService;
    private final FollowClient followClient;

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

    public Map<Long, String> getMembersName(List<Long> memberIds) {

        if (memberIds == null || memberIds.isEmpty())
            return Map.of();

        List<MemberIdNameDto> memberIdNameDtos = memberRepository.findIdAndNameByIdIn(memberIds);
        return memberIdNameDtos.stream()
                .collect(Collectors.toMap(
                        MemberIdNameDto::id,
                        MemberIdNameDto::name
                ));
    }

    public boolean getMemberExists(Long memberId) {

        return memberRepository.existsById(memberId);
    }

    public ChatProfileResponseDto getMemberInfo(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NotFoundMember::new);

        return ChatProfileResponseDto.of(member);
    }

    public Map<Long, MemberProfileResponseDto> getMembersProfile(List<Long> memberIds) {
        
        if (memberIds == null || memberIds.isEmpty()) {
            return Map.of();
        }
        
        List<Member> members = memberRepository.findAllById(memberIds);
        
        return members.stream()
                .collect(Collectors.toMap(
                        Member::getMemberId,
                        member -> new MemberProfileResponseDto(
                                member.getNickname(),
                                member.getProfileImage(),
                                member.getUserRating()
                        )
                ));
    }

    public boolean isOnboardingCompleted(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        return member.isOnboarded();
    }

    @Transactional
    public void completeOnboarding(Long memberId, OnboardingRequestDto onboardingRequestDto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        // 카카오 API로 동네명 해석
        String currentTown = locationService.resolveTown(
                onboardingRequestDto.longitude(),
                onboardingRequestDto.latitude()
        );

        member.completeOnboarding(
                onboardingRequestDto.phoneNumber(),
                currentTown,
                onboardingRequestDto.longitude(), //경도
                onboardingRequestDto.latitude(), //위도
                onboardingRequestDto.nickname(),
                onboardingRequestDto.profileImage(),
                onboardingRequestDto.availableTime()
        );
    }

    public DefaultProfileResponseDto getDefaultProfile(Long memberId) {

        memberRepository.findById(memberId).orElseThrow(MemberException.NoAuthMember::new);

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

        //중복체크: 온보딩이 아닐 때는 기존 닉네임과 같아도 사용 가능하게 처리
        if (!nickname.equals(member.getNickname()) && memberRepository.existsByNickname(nickname)) {
            throw new MemberException.NicknameAlreadyExists();
        }

        return true;
    }

    @Transactional
    public Boolean saveStarRating(Long fromMemberId, StarRatingRequestDto starRatingRequestDto) {
        memberRepository.findById(fromMemberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        Member member = memberRepository.findById(starRatingRequestDto.memberId())
                .orElseThrow(MemberException.NotFoundMember::new);

        // 자기 자신을 평가하는 경우 예외 처리 (선택적)
        if (fromMemberId.equals(starRatingRequestDto.memberId())) {
            throw new MemberException.SelfRatingNotAllowed();
        }

        // 평점 추가
        member.addStarRating(starRatingRequestDto.starRating());
        memberRepository.save(member);

        return true;
    }

    @Transactional
    public void updateProfile(Long memberId, UpdateProfileRequestDto updateProfileRequestDto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        member.updateProfile(
                updateProfileRequestDto.nickname(),
                updateProfileRequestDto.profileImage(),
                updateProfileRequestDto.availableTime()
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

    public StarRatingResponseDto getStarRatingOfPartner(Long memberId, Long partnerId) {
        memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        Member partner = memberRepository.findById(partnerId)
                .orElseThrow(MemberException.NotFoundMember::new);

        return StarRatingResponseDto.builder()
                .memberId(partner.getMemberId())
                .userRatingSum(partner.getUserRatingSum())
                .userRatingCount(partner.getUserRatingCount())
                .userRating(partner.getUserRating())
                .build();
    }

    public String getTown(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        return member.getCurrentTown();
    }

    @Transactional
    public String updateTown(Long memberId, UpdateTownRequestDto updateTownRequestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        // 범위 검증 (-90~90, -180~180)
        double lat = updateTownRequestDto.latitude().doubleValue();
        double lon = updateTownRequestDto.longitude().doubleValue();
        if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
            throw new MemberException.TownResolvedFailed();
        }

        // 경위도 -> 동네명
        String currentTown = locationService.resolveTown(updateTownRequestDto.longitude(), updateTownRequestDto.latitude());

        member.updateTown(updateTownRequestDto.longitude(), updateTownRequestDto.latitude(), currentTown);
        return currentTown;
    }
    
    public String getTownByCoordinates(BigDecimal longitude, BigDecimal latitude) {
        // 범위 검증 (-90~90, -180~180)
        if (latitude != null && longitude != null) {
            double lat = latitude.doubleValue();
            double lon = longitude.doubleValue();
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                throw new MemberException.TownResolvedFailed();
            }
        }
        return locationService.resolveTown(longitude, latitude);
    }

    public ProfileResponseDto getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        return ProfileResponseDto.builder()
                .memberId(member.getMemberId())
                .profileImage(member.getProfileImage())
                .nickname(member.getNickname())
                .currentTown(member.getCurrentTown())
                .userRating(member.getUserRating())
                .userRatingCount(member.getUserRatingCount())
                .availableTime(member.getAvailableTime())
                .build();
    }

    public PartnerProfileResponseDto getPartnerProfile(Long memberId, Long partnerId) {
        memberRepository.findById(memberId).orElseThrow(MemberException.NoAuthMember::new);

        Member partner = memberRepository.findById(partnerId)
                .orElseThrow(MemberException.NotFoundMember::new);

        return PartnerProfileResponseDto.builder()
                .memberId(partner.getMemberId())
                .profileImage(partner.getProfileImage())
                .nickname(partner.getNickname())
                .currentTown(partner.getCurrentTown())
                .userRating(partner.getUserRating())
                .userRatingCount(partner.getUserRatingCount())
                .availableTime(partner.getAvailableTime())
                .isFollowing(followClient.isFollowing(memberId, partnerId))
                .build();
    }

    public List<MemberSearchResponseDto> searchByNickname(Long loginMemberId, String keyword) {
        List<Member> members = memberRepository.findByNicknameContainingIgnoreCase(keyword);

        // 검색 리스트에 자기 자신 제외
        List<Long> targetIds = members.stream()
                .map(Member::getMemberId)
                .filter(id -> !id.equals(loginMemberId))
                .toList();

        // 팔로우 여부 벌크 조회
        Map<Long, Boolean> followMap = targetIds.isEmpty()
                ? Map.of()
                : followClient.isFollowingBulk(new FollowBulkCheckRequestDto(loginMemberId, targetIds));

        return members.stream()
                .filter(m -> !m.getMemberId().equals(loginMemberId))  // ← 이 줄 추가하면 “자기 자신”은 리스트에서 제거
                .map(m -> MemberSearchResponseDto.builder()
                        .memberId(m.getMemberId())
                        .nickname(m.getNickname())
                        .profileImage(m.getProfileImage())
                        .isFollowing(followMap.getOrDefault(m.getMemberId(), false))
                        .build())
                .toList();
    }

    @Transactional
    public void follow(Long memberId, Long partnerMemberId) {

        memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        memberRepository.findById(partnerMemberId)
                .orElseThrow(MemberException.NotFoundMember::new);

        followClient.follow(memberId, partnerMemberId);
    }

    @Transactional
    public void unfollow(Long memberId, Long partnerMemberId) {
        memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        memberRepository.findById(partnerMemberId)
                .orElseThrow(MemberException.NotFoundMember::new);

        followClient.unfollow(memberId, partnerMemberId);
    }

    public List<FollowingResponseDto> getMyFollowing(Long memberId) {
        // 1) 팔로잉 대상 ID 목록 조회
        List<Long> followingIds = followClient.getFollowingIds(memberId);
        if (followingIds.isEmpty()) return List.of();

        // 2) 대상 회원 프로필 일괄 조회
        List<Member> members = memberRepository.findAllById(followingIds);

        // 3) 요청 순서 유지하여 DTO 매핑
        Map<Long, Member> byId = members.stream()
                .collect(Collectors.toMap(Member::getMemberId, m -> m));

        List<FollowingResponseDto> result = new ArrayList<>(followingIds.size());
        for (Long id : followingIds) {
            Member m = byId.get(id);
            if (m == null) continue; // 동시성으로 삭제된 경우 방어
            result.add(FollowingResponseDto.builder()
                    .memberId(m.getMemberId())
                    .nickname(m.getNickname())
                    .profileImage(m.getProfileImage())
                    .build());
        }
        return result;
    }

    public List<FollowerResponseDto> getMyFollowers(Long memberId) {
        // 1) 나를 팔로우하는 대상 ID 목록
        List<Long> followerIds = followClient.getFollowerIds(memberId);
        if (followerIds.isEmpty()) return List.of();

        // 2) 대상 회원 프로필 일괄 조회
        List<Member> members = memberRepository.findAllById(followerIds);

        // 3) 내가 팔로우하는지 여부 확인 (bulk)
        Map<Long, Boolean> followMap = followClient.isFollowingBulk(
                new FollowBulkCheckRequestDto(memberId, followerIds));
        
        // 4) 요청 순서 유지하여 DTO 매핑
        Map<Long, Member> byId = members.stream()
                .collect(Collectors.toMap(Member::getMemberId, m -> m));

        List<FollowerResponseDto> result = new ArrayList<>(followerIds.size());
        for (Long id : followerIds) {
            Member m = byId.get(id);
            if (m == null) continue;
            result.add(FollowerResponseDto.builder()
                    .memberId(m.getMemberId())
                    .nickname(m.getNickname())
                    .profileImage(m.getProfileImage())
                    .isFollowing(followMap.getOrDefault(m.getMemberId(), false))
                    .build());
        }
        return result;
    }
}
