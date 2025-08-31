package kr.co.readingtown.chat.integration.bookhouse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookhouseUpdater {

    private final BookhouseClient bookhouseClient;

    // 교환 완료: RESERVED -> EXCHANGED
    public void completeExchange(Long chatroomId) {
        bookhouseClient.completeExchange(chatroomId);
    }

    // 반납 완료: EXCHANGED -> PENDING
    public void returnExchange(Long chatroomId) {
        bookhouseClient.returnExchange(chatroomId);
    }
}