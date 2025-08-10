package kr.co.readingtown.member.dto.response;

import kr.co.readingtown.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MemberSearchResponseDto {
    private final Long memberId;
    private final String nickname;
    private final String profileImage;
    private final boolean followed; //내가 팔로우 중인지

    public static MemberSearchResponseDto from(Member m) {
        return MemberSearchResponseDto.builder()
                .memberId(m.getMemberId())
                .nickname(m.getNickname())
                .profileImage(m.getProfileImage())
                .build();
    }
}
