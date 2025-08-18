package kr.co.readingtown.member.dto.response;

import kr.co.readingtown.member.domain.Member;

public record ChatProfileResponseDto(
        Long memberId,
        String nickname,
        String profileImage
) {

    public static ChatProfileResponseDto of(Member member) {

        return new ChatProfileResponseDto(
                member.getMemberId(),
                member.getNickname(),
                member.getProfileImage()
        );
    }
}
