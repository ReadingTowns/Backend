package kr.co.readingtown.member.service;

import kr.co.readingtown.member.client.AiRecommendClient;
import kr.co.readingtown.member.client.BookhouseClient;
import kr.co.readingtown.member.dto.response.ai.BookRecommendation;
import kr.co.readingtown.member.dto.response.ai.BookRecommendationResponseDto;
import kr.co.readingtown.member.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final BookhouseClient bookhouseClient;
    private final AiRecommendClient aiRecommendClient;
    private final KeywordRepository keywordRepository;

    /**
     * 유저 서재에 있는 책 id 리스트
     * 유저가 선택한 키워드 id 리스트
     * AI 서버 /recommend API 호출
     */
    public List<BookRecommendationResponseDto> recommendBooks(Long memberId) {

        // 유저 서재 책 id 추출
        List<Long> bookIds = bookhouseClient.getMembersBookId(memberId);
        if (bookIds.isEmpty())
            return List.of();

        // 유저 키워드 추출
        List<String> keywords = keywordRepository.findContentsByMemberId(memberId);
        if (keywords.isEmpty())
            return List.of();

        // 파라미터로 넘길 수 있도록 가공
        String bookIdsParam = bookIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
        String keywordsParam = String.join(",", keywords);

        // AI 서버 호출
        List<BookRecommendation> recommendations = aiRecommendClient
                .recommend(bookIdsParam, keywordsParam)
                .recommendations();

        // response 가공
        return recommendations.stream()
                .map(b -> new BookRecommendationResponseDto(
                        b.bookId(),
                        b.bookName(),
                        b.author(),
                        b.publisher(),
                        BigDecimal.valueOf(b.similarity() * 100)
                                .setScale(1, RoundingMode.HALF_UP)
                                .doubleValue()
                ))
                .collect(Collectors.toList());
    }

    public void recommendMembers() {

    }
}
