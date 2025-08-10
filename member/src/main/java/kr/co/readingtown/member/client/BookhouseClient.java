package kr.co.readingtown.member.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "book-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {

    @PostMapping("/internal/bookhouses")
    void registerBookhouse(@RequestParam("memberId") Long memberId);

    @GetMapping("/internal/bookhouses/id")
    Long getBookhouseId(@RequestParam("memberId") Long memberId);
}
