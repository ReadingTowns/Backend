package kr.co.readingtown.bookhouse.integration.member;

import kr.co.readingtown.bookhouse.dto.request.FollowBulkCheckRequestDto;
import kr.co.readingtown.bookhouse.dto.response.MemberProfileResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "bookhouse-member-client",
        url = "${server.base-uri}"
)
public interface MemberClient {
    
    @PostMapping("/internal/members/profiles")
    Map<Long, MemberProfileResponseDto> getMembersProfile(@RequestBody List<Long> memberIds);
    
    @PostMapping("/internal/follows/is-following-bulk")
    Map<Long, Boolean> checkFollowing(@RequestBody FollowBulkCheckRequestDto requestDto);
}
