package kr.co.readingtown.member.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "book-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {

}
