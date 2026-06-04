package kr.co.readingtown.member.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import kr.co.readingtown.member.client.AiRecommendClient;
import kr.co.readingtown.member.dto.response.ai.BookRecommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiBookRecommendService {

    private final AiRecommendClient aiRecommendClient;

    /**
     * AI 서버 도서 추천 호출
     * 실패 시 지수적 Backoff로 최대 3회 재시도 (1s → 2s → 4s)
     */
    @CircuitBreaker(name = "aiCircuitBreaker", fallbackMethod = "recommendFallback")
    @Retry(name = "aiRetry", fallbackMethod = "recommendFallback")
    public List<BookRecommendation> recommend(List<Long> bookIds, List<String> keywords) {
        String bookIdsParam = bookIds.isEmpty() ? null
                : bookIds.stream().map(String::valueOf).collect(Collectors.joining(","));
        String keywordsParam = keywords.isEmpty() ? null
                : String.join(",", keywords);

        return aiRecommendClient.recommend(bookIdsParam, keywordsParam).recommendations();
    }

    private List<BookRecommendation> recommendFallback(List<Long> bookIds, List<String> keywords, Exception e) {
        log.error("AI 추천 서버 호출 실패. 3회 재시도 후 빈 리스트 반환.", e);
        return List.of();
    }
}
