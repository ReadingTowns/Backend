package kr.co.readingtown.member.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "member-chat-client",
        url = "${server.base-uri}"
)
public interface ChatClient {

    @DeleteMapping("/internal/chat/revoke")
    void deleteChatroom(@RequestParam Long memberId);
}
