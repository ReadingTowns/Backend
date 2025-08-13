package kr.co.readingtown.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StarRatingResponseDto {
    private Long memberId;
    private int userRatingSum;
    private int userRatingCount;
    private Double userRating;
}
