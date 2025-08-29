package kr.co.readingtown.member.integration.bookhouse;

import kr.co.readingtown.member.dto.response.internal.ExchangingBookResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "member-bookhouse-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {
    
    @GetMapping("/internal/members/{memberId}/exchanges")
    List<ExchangingBookResponseDto> getExchangingBooks(@PathVariable("memberId") Long memberId);
}