package kr.co.readingtown.member.service;

import kr.co.readingtown.common.config.AppProperties;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import kr.co.readingtown.member.exception.MemberException;
import kr.co.readingtown.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        if (phoneNumber == null || phoneNumber.isBlank()
                || username == null || username.isBlank()) {
            throw new MemberException.MissingRequiredField();
        }

        Member member = memberRepository.findById(memberId).orElseThrow(MemberException.NoAuthMember::new);

        if(profileImage == null || profileImage.isBlank()) {
            profileImage = appProperties.getDefaultProfileImageUrl();
        }
        member.completeOnboarding(phoneNumber, currentTown, username, profileImage, availableTime);
    }
}
