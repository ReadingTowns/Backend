package kr.co.readingtown.member.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "bookhouse-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {

    @GetMapping("/internal/bookhouse/books")
    List<Long> getMembersBookId(@RequestParam("memberId") Long memberId);

}
