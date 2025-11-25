package kr.co.readingtown.member.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "member-review-client",
        url = "${server.base-uri}"
)
public interface ReviewClient {

    @DeleteMapping("/internal/reviews/revoke")
    void deleteMembersReviews(@RequestParam("memberId") Long memberId);
}
