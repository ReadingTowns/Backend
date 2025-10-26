package kr.co.readingtown.member.service;

import kr.co.readingtown.member.client.AiRecommendClient;
import kr.co.readingtown.member.client.BookhouseClient;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.dto.response.LocalMemberRecommendationDto;
import kr.co.readingtown.member.dto.response.ai.BookRecommendation;
import kr.co.readingtown.member.dto.response.ai.BookRecommendationResponseDto;
import kr.co.readingtown.member.exception.MemberException;
import kr.co.readingtown.member.repository.KeywordRepository;
import kr.co.readingtown.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final BookhouseClient bookhouseClient;
    private final AiRecommendClient aiRecommendClient;
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;

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

    /**
     * 동네 기반 유저 추천
     * Haversine 공식을 사용하여 거리 계산
     */
    public List<LocalMemberRecommendationDto> getLocalMemberRecommendations(Long memberId) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(MemberException.MemberNotFound::new);
        
        if (currentMember.getLatitude() == null || currentMember.getLongitude() == null) {
            return List.of();
        }
        
        List<Member> allMembers = memberRepository.findAllWithLocation();
        
        return allMembers.stream()
                .filter(member -> !member.getMemberId().equals(memberId))
                .filter(member -> member.getLatitude() != null && member.getLongitude() != null)
                .map(member -> {
                    double distance = calculateDistance(
                            currentMember.getLatitude().doubleValue(),
                            currentMember.getLongitude().doubleValue(),
                            member.getLatitude().doubleValue(),
                            member.getLongitude().doubleValue()
                    );
                    return LocalMemberRecommendationDto.from(member, distance);
                })
                .sorted(Comparator.comparing(LocalMemberRecommendationDto::distanceKm))
                .limit(10)
                .collect(Collectors.toList());
    }
    
    /**
     * Haversine 공식으로 두 지점 간 거리 계산 (km)
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // 지구 반지름 (km)
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}
