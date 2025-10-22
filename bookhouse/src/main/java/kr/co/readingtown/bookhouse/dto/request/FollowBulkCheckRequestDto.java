package kr.co.readingtown.bookhouse.dto.request;

import java.util.List;

public record FollowBulkCheckRequestDto(
        Long fromMemberId,
        List<Long> targetMemberIds
) {}