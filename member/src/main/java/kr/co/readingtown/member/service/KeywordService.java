package kr.co.readingtown.member.service;

import kr.co.readingtown.member.client.YoutubeSearchClient;
import kr.co.readingtown.member.domain.Keyword;
import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.MemberKeyword;
import kr.co.readingtown.member.domain.enums.KeywordType;
import kr.co.readingtown.member.dto.request.KeywordRequest;
import kr.co.readingtown.member.dto.response.KeywordDetailResponse;
import kr.co.readingtown.member.dto.response.KeywordResponse;
import kr.co.readingtown.member.dto.response.YoutubeSearchResponse;
import kr.co.readingtown.member.dto.response.YoutubeSearchedData;
import kr.co.readingtown.member.exception.KeywordException;
import kr.co.readingtown.member.exception.MemberException;
import kr.co.readingtown.member.repository.KeywordRepository;
import kr.co.readingtown.member.repository.MemberKeywordRepository;
import kr.co.readingtown.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final MemberKeywordRepository memberKeywordRepository;

    // 키워드 후보지 조회
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

    // 사용자 키워드 저장
    @Transactional
    public void saveKeyword(Long memberId, KeywordRequest keywordRequest) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberException.NoAuthMember::new);

        List<MemberKeyword> memberKeywords = new ArrayList<>();
        for(Long id : keywordRequest.keywordIds()) {

            MemberKeyword memberKeyword = MemberKeyword.builder()
                    .memberId(memberId)
                    .keywordId(id)
                    .build();
            memberKeywords.add(memberKeyword);
        }
        memberKeywordRepository.saveAll(memberKeywords);
    }

    // 사용자가 선택한 키워드 조회
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

}
