package kr.co.readingtown.social.internalapi;

import kr.co.readingtown.social.dto.request.FollowBulkCheckRequestDto;
import kr.co.readingtown.social.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public Map<Long, Boolean> isFollowingBulk(@RequestBody @Validated FollowBulkCheckRequestDto followBulkCheckRequestDto) {
        return followService.isFollowingMany(
                followBulkCheckRequestDto.getFromMemberId(),
                followBulkCheckRequestDto.getTargetMemberIds()
        );
    }
}
