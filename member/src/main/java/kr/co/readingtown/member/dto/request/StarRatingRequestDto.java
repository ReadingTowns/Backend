package kr.co.readingtown.member.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record StarRatingRequestDto(
    Long memberId,
    
    @Min(1)
    @Max(5)
    int starRating
) {
}
