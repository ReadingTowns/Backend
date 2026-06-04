package kr.co.readingtown.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import kr.co.readingtown.member.dto.response.ai.BookRecommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationCacheService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final long CACHE_TTL_DAYS = 7;
    private static final String BOOK_RECOMMEND_KEY_PREFIX = "recommend:v1:user:";


    /**
     * 추천 결과 캐시 조회
     * @param memberId 사용자 ID
     * @return 캐시 HIT 시 추천 결과, MISS 시 Optional.empty()
     */
    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "getRecommendationsFallback")
    @Retry(name = "redisRetry", fallbackMethod = "getRecommendationsFallback")
    public Optional<List<BookRecommendation>> getRecommendations(Long memberId) {

        String key = BOOK_RECOMMEND_KEY_PREFIX + memberId;
        String value = redisTemplate.opsForValue().get(key);
        if (!StringUtils.hasText(value)) {
            return Optional.empty();
        }

        try {
            List<BookRecommendation> recommendations =
                    objectMapper.readValue(value, new TypeReference<List<BookRecommendation>>() {});

            return Optional.of(recommendations);
        } catch (JsonProcessingException e) {
            log.error("추천 캐시 파싱 실패. memberId: {}", memberId, e);
            return Optional.empty();
        }
    }

    private Optional<List<BookRecommendation>> getRecommendationsFallback(Long memberId, Exception e) {
        log.error("Redis 조회 실패. 3회 재시도 후 AI 서버로 fallback. memberId: {}", memberId, e);
        return Optional.empty();
    }

    /**
     * 추천 결과 캐시 저장
     * @param memberId 사용자 ID
     * @param recommendations AI 서버 추천 결과
     */
    @CircuitBreaker(name = "redisCircuitBreaker", fallbackMethod = "saveRecommendationsFallback")
    @Retry(name = "redisRetry", fallbackMethod = "saveRecommendationsFallback")
    public void saveRecommendations(Long memberId, List<BookRecommendation> recommendations) {

        String key = BOOK_RECOMMEND_KEY_PREFIX + memberId;

        try {
            String value = objectMapper.writeValueAsString(recommendations);
            redisTemplate.opsForValue().set(key, value, CACHE_TTL_DAYS, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            log.error("추천 캐시 저장 실패. memberId: {}", memberId, e);
        }
    }

    private void saveRecommendationsFallback(Long memberId, List<BookRecommendation> recommendations, Exception e) {
        log.error("Redis 저장 실패. 3회 재시도 후 캐싱 생략. memberId: {}", memberId, e);
    }

    /**
     * 추천 결과 캐시 삭제
     * 책 목록 또는 키워드 변경 시 호출
     * @param memberId 사용자 ID
     */
    public void evictRecommendations(Long memberId) {

        try {
            redisTemplate.delete(BOOK_RECOMMEND_KEY_PREFIX + memberId);
        } catch (Exception e) {
            log.error("추천 캐시 삭제 실패. memberId: {}", memberId, e);
        }
    }
}
