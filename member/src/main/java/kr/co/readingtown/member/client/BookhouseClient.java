package kr.co.readingtown.member.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(
        name = "bookhouse-client",
        url = "${server.base-uri}"
)
public interface BookhouseClient {

}
