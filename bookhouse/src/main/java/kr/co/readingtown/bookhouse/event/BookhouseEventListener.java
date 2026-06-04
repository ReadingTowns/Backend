package kr.co.readingtown.bookhouse.event;

import kr.co.readingtown.bookhouse.integration.member.MemberClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class BookhouseEventListener {

    private final MemberClient memberClient;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBookhouseChanged(BookhouseChangedEvent event) {
        memberClient.evictRecommendationCache(event.memberId());
    }
}
