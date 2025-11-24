package kr.co.readingtown.social.internalapi;

import jakarta.validation.Valid;
import kr.co.readingtown.social.dto.request.FollowBulkCheckRequestDto;
import kr.co.readingtown.social.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/follows")
public class InternalFollowController {

    private final FollowService followService;

    // 단건 체크: A가 B를 팔로우 중인가?
    @GetMapping("/is-following")
    public boolean isFollowing(@RequestParam("fromMemberId") Long fromMemberId,
                               @RequestParam("toMemberId") Long toMemberId) {
        return followService.isFollowing(fromMemberId, toMemberId);
    }

    // 대량 체크: targets=1,2,3
    @PostMapping("/is-following-bulk")
    public Map<Long, Boolean> isFollowingBulk(@RequestBody @Valid FollowBulkCheckRequestDto followBulkCheckRequestDto) {
        return followService.isFollowingMany(
                followBulkCheckRequestDto.getFromMemberId(),
                followBulkCheckRequestDto.getTargetMemberIds()
        );
    }

    @PostMapping
    public void follow(@RequestParam("fromMemberId") Long fromMemberId, @RequestParam("toMemberId") Long toMemberId){
        followService.follow(fromMemberId, toMemberId);
    }

    @DeleteMapping
    public void unfollow(@RequestParam("fromMemberId") Long fromMemberId, @RequestParam("toMemberId") Long toMemberId){
        followService.unfollow(fromMemberId, toMemberId);
    }

    // 내가 팔로우 중인 대상들의 ID 목록
    @GetMapping("/following-ids")
    public List<Long> getFollowingIds(@RequestParam("memberId") Long memberId){
        return followService.getFollowingIds(memberId);
    }

    // 나를 팔로우하는 대상들의 ID 목록
    @GetMapping("/follower-ids")
    public List<Long> getFollowerIds(@RequestParam("memberId") Long memberId){
        return followService.getFollowerIds(memberId);
    }

    // 회원 탈퇴 : 연관된 팔로잉 삭제
    @DeleteMapping("/revoke")
    public void deleteFollowRelation(@RequestParam("memberId") Long memberId) {

        followService.deleteFollowRelation(memberId);
    }
}
