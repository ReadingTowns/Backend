package kr.co.readingtown.chat.integration.member;

import kr.co.readingtown.chat.dto.response.ChatMemberInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "chat-member-client",
        url = "${server.base-uri}"
)
public interface MemberClient {

    @GetMapping("/internal/members/{memberId}/info")
    ChatMemberInfoResponse getMemberInfo(@PathVariable Long memberId);

    @GetMapping("/internal/members/id")
    Long getMemberId(@RequestParam("provider") String loginType,
                     @RequestParam("providerId") String loginId);
}
