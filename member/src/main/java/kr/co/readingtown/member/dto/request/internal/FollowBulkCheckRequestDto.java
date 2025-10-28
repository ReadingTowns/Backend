package kr.co.readingtown.member.dto.request.internal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record FollowBulkCheckRequestDto(
    @NotNull
    Long fromMemberId,
    
    @NotEmpty
    List<Long> targetMemberIds
) {
}