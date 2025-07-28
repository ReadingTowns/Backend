package kr.co.readingtown.authentication.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "member-client",
        url = "${server.base-uri}"
)
public interface MemberClient {

    @PostMapping("/internal/members")
    void registerMember(@RequestParam("provider") String loginType,
                      @RequestParam("providerId") String loginId,
                      @RequestParam("username") String username);

    @GetMapping("/internal/members/id")
    Long getMemberId(@RequestParam("provider") String loginType,
                     @RequestParam("providerId") String loginId);
}
