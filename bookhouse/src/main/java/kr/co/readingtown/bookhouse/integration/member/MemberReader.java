package kr.co.readingtown.bookhouse.integration.member;

import kr.co.readingtown.bookhouse.dto.request.FollowBulkCheckRequestDto;
import kr.co.readingtown.bookhouse.dto.response.MemberProfileResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("bookhouseMemberReader")
@RequiredArgsConstructor
public class MemberReader {
    
    private final MemberClient memberClient;
    
    public Map<Long, MemberProfileResponseDto> getMembersProfile(List<Long> memberIds) {
        if (memberIds == null || memberIds.isEmpty()) {
            return Map.of();
        }
        return memberClient.getMembersProfile(memberIds);
    }
    
    public Map<Long, Boolean> checkFollowing(Long memberId, List<Long> targetMemberIds) {
        if (memberId == null || targetMemberIds == null || targetMemberIds.isEmpty()) {
            return Map.of();
        }
        FollowBulkCheckRequestDto requestDto = new FollowBulkCheckRequestDto(memberId, targetMemberIds);
        return memberClient.checkFollowing(requestDto);
    }
}