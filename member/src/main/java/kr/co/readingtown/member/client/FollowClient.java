package kr.co.readingtown.member.client;

import kr.co.readingtown.member.dto.request.internal.FollowBulkCheckRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "member-follow-client",
        url = "${server.base-uri}"
)
public interface FollowClient {

    // 프로필 조회에 사용되는 팔로우 여부 확인
    @GetMapping("/internal/follows/is-following")
    boolean isFollowing(@RequestParam("fromMemberId") Long fromMemberId, @RequestParam("toMemberId") Long toMemberId);

    // 유저 검색에 사용되는 팔로우 여부 확인
    @PostMapping("/internal/follows/is-following-bulk")
    Map<Long, Boolean> isFollowingBulk(@RequestBody FollowBulkCheckRequestDto followBulkCheckRequestDto);

    @PostMapping("/internal/follows")
    void follow(@RequestParam("fromMemberId") Long fromMemberId, @RequestParam("toMemberId") Long toMemberId);

    @DeleteMapping("/internal/follows")
    void unfollow(@RequestParam("fromMemberId") Long fromMemberId, @RequestParam("toMemberId") Long toMemberId);

    // 내가 팔로우 중인 대상들의 ID 목록
    @GetMapping("/internal/follows/following-ids")
    List<Long> getFollowingIds(@RequestParam("memberId") Long memberId);

    // 나를 팔로우하는 대상들의 ID 목록
    @GetMapping("/internal/follows/follower-ids")
    List<Long> getFollowerIds(@RequestParam("memberId") Long memberId);

    // 회원 탈퇴 : 연관된 팔로잉 삭제
    @DeleteMapping("/internal/follows/revoke")
    void deleteFollowRelation(@RequestParam("memberId") Long memberId);
}
