package kr.co.readingtown.bookhouse.dto.request;

import java.util.List;

public record FollowBulkCheckRequestDto(
        Long fromMemberId,
        List<Long> targetMemberIds
) {
    public FollowBulkCheckRequestDto(Long fromMemberId, List<Long> targetMemberIds) {
        this.fromMemberId = fromMemberId;
        this.targetMemberIds = targetMemberIds;
    }
    
    public Long getFromMemberId() {
        return fromMemberId;
    }
    
    public List<Long> getTargetMemberIds() {
        return targetMemberIds;
    }
}