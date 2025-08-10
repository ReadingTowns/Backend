package kr.co.readingtown.member.service;

import kr.co.readingtown.common.config.AppProperties;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import kr.co.readingtown.member.dto.DefaultProfileResponseDto;
import kr.co.readingtown.member.dto.OnboardingRequestDto;
import kr.co.readingtown.member.dto.query.MemberIdNameDto;
import kr.co.readingtown.member.exception.MemberException;
import kr.co.readingtown.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final AppProperties appProperties;

    @Transactional
    public void registerMember(LoginType loginType, String loginId, String username) {

        if (!memberRepository.existsByLoginTypeAndLoginId(loginType, loginId)) {

            Member newMember = Member.builder()
                    .loginType(loginType)
                    .loginId(loginId)
                    .username(username)
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

    @Transactional
    public void completeOnboarding(Long memberId, OnboardingRequestDto onboardingRequestDto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        member.completeOnboarding(
                onboardingRequestDto.getPhoneNumber(),
                onboardingRequestDto.getCurrentTown(),
                onboardingRequestDto.getUsername(),
                onboardingRequestDto.getProfileImage(),
                onboardingRequestDto.getAvailableTime()
        );
    }

    public DefaultProfileResponseDto getDefaultProfile(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NoAuthMember::new);

        String defaultUsername = generateUniqueUsername();
        String defaultImageUrl = appProperties.getDefaultProfileImageUrl();

        return new DefaultProfileResponseDto(defaultUsername, defaultImageUrl);
    }

    //랜덤 닉네임 설정
    private String generateUniqueUsername() {

        String prefix = "리딩여우";
        String candidate;
        int attempt = 0;

        //리딩여우+랜덤숫자문자 생성
        do {
            String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
            candidate = prefix + random;
            attempt++;
            if (attempt > 10) {
                throw new MemberException.UsernameGenerationFailed();
            }
        } while (memberRepository.existsByUsername(candidate));

        return candidate;
    }

    //닉네임 사용가능 여부 확인
    public boolean isUsernameAvailable(String username) {
        //null 체크
        if (username == null || username.isBlank()) {
            throw new MemberException.InvalidUsername();
        }

        // 길이 제한 체크
        if (username.length() < 2 || username.length() > 20) {
            throw new MemberException.InvalidUsername();
        }

        //중복체크
        if (memberRepository.existsByUsername(username)) {
            throw new MemberException.UsernameAlreadyExists();
        }

        return true;
    }

}
