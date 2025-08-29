package kr.co.readingtown.member.integration.bookhouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "bookhouse-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {
    
    @GetMapping("/internal/members/{memberId}/exchanges")
    List<ExchangingBookResponse> getExchangingBooks(@PathVariable("memberId") Long memberId);
}