package kr.co.readingtown.review.integration.member;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "review-member-client",
        url = "${server.base-uri}"
)
public interface MemberClient {

    @PostMapping("/internal/members/names")
    Map<Long, String> getMembersName(@RequestBody List<Long> memberIds);

    @GetMapping("/internal/members/{memberId}/exists")
    boolean existsMember(@PathVariable("memberId") Long memberId);
}
