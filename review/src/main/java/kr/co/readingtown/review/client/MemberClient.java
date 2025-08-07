package kr.co.readingtown.review.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "review-member-client",
        url = "${server.base-uri}"
)
public interface MemberClient {

    @PostMapping("/internal/members/name")
    Map<Long, String> getMembersName(@RequestBody List<Long> memberIds);
}
