package kr.co.readingtown.member.service;

import kr.co.readingtown.common.config.AppProperties;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import kr.co.readingtown.member.dto.DefaultProfileResponseDto;
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

    @Transactional
    public void completeOnboarding(Long memberId, String phoneNumber, String currentTown, String username, String profileImage, String availableTime) {

        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NoAuthMember::new);

        if (phoneNumber == null || phoneNumber.isBlank()
                || currentTown == null || currentTown.isBlank()
                || username == null || username.isBlank()
                || profileImage == null || profileImage.isBlank()) {
            throw new MemberException.MissingRequiredField();
        }

        member.completeOnboarding(phoneNumber, currentTown, username, profileImage, availableTime);
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
