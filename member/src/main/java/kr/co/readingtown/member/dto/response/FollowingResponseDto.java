package kr.co.readingtown.member.dto.response;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FollowingResponseDto {
    private Long memberId;
    private String nickname;
    private String profileImage;
}
