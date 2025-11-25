package kr.co.readingtown.chat.integration.bookhouse;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "chat-exchange-status-client",
        url = "${server.base-uri}"
)
public interface ExchangeStatusClient {

    @DeleteMapping("/internal/exchange-status/revoke")
    void deleteExchangeStatusByChatroom(@RequestParam(name = "chatroomIds") List<Long> chatroomIds);
}
