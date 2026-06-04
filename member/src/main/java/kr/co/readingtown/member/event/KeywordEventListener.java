package kr.co.readingtown.member.event;

import kr.co.readingtown.member.service.RecommendationCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeywordEventListener {

    private final RecommendationCacheService recommendationCacheService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onKeywordChanged(KeywordChangedEvent event) {
        try {
            recommendationCacheService.evictRecommendations(event.memberId());
        } catch (Exception e) {
            log.error("추천 캐시 무효화 실패. memberId: {}", event.memberId(), e);
        }
    }
}
