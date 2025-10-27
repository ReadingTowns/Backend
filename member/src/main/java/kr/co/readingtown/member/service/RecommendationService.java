package kr.co.readingtown.member.service;

import kr.co.readingtown.member.client.AiRecommendClient;
import kr.co.readingtown.member.client.BookhouseClient;
import kr.co.readingtown.member.client.YoutubeSearchClient;
import kr.co.readingtown.member.domain.Keyword;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.MemberKeyword;
import kr.co.readingtown.member.domain.enums.KeywordType;
import kr.co.readingtown.member.dto.request.KeywordRequest;
import kr.co.readingtown.member.dto.response.*;
import kr.co.readingtown.member.dto.response.ai.BookRecommendation;
import kr.co.readingtown.member.dto.response.ai.BookRecommendationResponseDto;
import kr.co.readingtown.member.dto.response.ai.UserRecommendation;
import kr.co.readingtown.member.dto.response.ai.UserRecommendationResponse;
import kr.co.readingtown.member.exception.KeywordException;
import kr.co.readingtown.member.exception.MemberException;
import kr.co.readingtown.member.repository.KeywordRepository;
import kr.co.readingtown.member.repository.MemberKeywordRepository;
import kr.co.readingtown.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final BookhouseClient bookhouseClient;
    private final AiRecommendClient aiRecommendClient;
    private final KeywordRepository keywordRepository;
    private final MemberRepository memberRepository;
    private final YoutubeSearchClient youtubeSearchClient;
    private final MemberKeywordRepository memberKeywordRepository;

    @Value("${youtube.key}")
    private String apiKey;

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
    public List<LocalMemberRecommendationDto> recommendLocalMembers(Long memberId) {
        Member currentMember = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NotFoundMember::new);
        
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
     * 취향 기반 유저 추천 (키워드 + 서재 책 기반)
     */
    public List<SimilarMemberRecommendationDto> recommendSimilarMembers(Long memberId) {
        try {
            // AI 서버 호출 (키워드 + 책 기반) recommendUsersByKeywords 로 변경 가능
            UserRecommendationResponse response = aiRecommendClient.recommendUsersCombined(memberId, 10);
            
            if (response == null || response.recommendations() == null || response.recommendations().isEmpty()) {
                return List.of();
            }
            
            // 추천된 member_id들로 실제 멤버 정보 조회
            List<Long> recommendedMemberIds = response.recommendations().stream()
                    .map(UserRecommendation::memberId)
                    .collect(Collectors.toList());
            
            List<Member> members = memberRepository.findAllById(recommendedMemberIds);
            
            // 멤버 정보와 추천 정보를 매칭하여 최종 DTO 생성
            return response.recommendations().stream()
                    .map(rec -> {
                        Member member = members.stream()
                                .filter(m -> m.getMemberId().equals(rec.memberId()))
                                .findFirst()
                                .orElse(null);
                        
                        if (member == null) return null;
                        
                        List<String> bookNames = rec.matchedBooks() != null 
                            ? rec.matchedBooks().stream()
                                .map(UserRecommendation.MatchedBook::bookName)
                                .collect(Collectors.toList())
                            : List.of();
                        
                        return SimilarMemberRecommendationDto.from(
                                member,
                                rec.similarity(),
                                rec.matchedKeywords() != null ? rec.matchedKeywords() : List.of(),
                                bookNames
                        );
                    })
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // AI 서버 오류 시 빈 리스트 반환
            log.error("AI 서버 유저 추천 실패");
            return List.of();
        }
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

    /*
    입력한 키워드 바탕으로 유튜브 영상 10개 반환
     */
    public List<YoutubeSearchResponse> searchVideo(String keyword) {

        String searchString = keyword + "관련 책 추천";

        YoutubeSearchedData response = youtubeSearchClient.searchVideos(
                "snippet",
                searchString,
                "video",
                10,
                "KR",
                apiKey
        );

        return response.items().stream()
                .map(item -> new YoutubeSearchResponse(
                        item.snippet().title(),
                        "https://www.youtube.com/watch?v=" + item.id().videoId(),
                        item.snippet().thumbnails().defaultThumbnail().url()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 키워드 후보지를 반환합니다.
     */
    public KeywordResponse getKeyword() {

        List<Keyword> keywords = keywordRepository.findAll();

        List<KeywordDetailResponse> moodKeywords = extractKeywordByType(keywords, KeywordType.MOOD);
        List<KeywordDetailResponse> genreKeywords = extractKeywordByType(keywords, KeywordType.GENRE);
        List<KeywordDetailResponse> contentKeywords = extractKeywordByType(keywords, KeywordType.CONTENT);

        return new KeywordResponse(moodKeywords, genreKeywords, contentKeywords);
    }

    private List<KeywordDetailResponse> extractKeywordByType(List<Keyword> keywords, KeywordType type) {
        return keywords.stream()
                .filter(keyword -> keyword.getType() == type)
                .map(keyword -> new KeywordDetailResponse(keyword.getKeywordId(), keyword.getContent()))
                .toList();
    }

    /**
     * 사용자가 선택한 키워드 조회
     */
    public List<KeywordDetailResponse> getMemberKeywords(Long memberId) {

        List<Long> keywordIds = memberKeywordRepository.findByMemberId(memberId);

        List<KeywordDetailResponse> responses = new ArrayList<>();
        for(Long keywordId : keywordIds) {

            Keyword keyword = keywordRepository.findById(keywordId)
                    .orElseThrow(KeywordException.NotFoundKeyword::new);
            responses.add(new KeywordDetailResponse(keywordId, keyword.getContent()));
        }
        return responses;
    }

    /**
     * 사용자 키워드 수정
     */
    @Transactional
    public void updateKeyword(Long memberId, KeywordRequest keywordRequest) {
        // 요청으로 들어온 키워드 id 목록 조회
        List<Long> requestedKeywordIds = keywordRequest.keywordIds();

        // 현재 DB에 저장된 키워드 목록 조회
        List<Long> existingKeywordIds = memberKeywordRepository.findByMemberId(memberId);

        // 새로 추가해야 하는 키워드 ID
        List<Long> toAdd = requestedKeywordIds.stream()
                .filter(id -> !existingKeywordIds.contains(id))
                .toList();

        // 삭제해야 하는 키워드 ID
        List<Long> toRemove = existingKeywordIds.stream()
                .filter(id -> !requestedKeywordIds.contains(id))
                .toList();

        // 삭제
        if (!toRemove.isEmpty()) {
            memberKeywordRepository.deleteAllByMemberIdAndKeywordIds(memberId, toRemove);
        }

        // 추가
        if (!toAdd.isEmpty()) {
            List<MemberKeyword> newKeywords = toAdd.stream()
                    .map(keywordId -> new MemberKeyword(memberId, keywordId))
                    .toList();
            memberKeywordRepository.saveAll(newKeywords);
        }
    }
}
