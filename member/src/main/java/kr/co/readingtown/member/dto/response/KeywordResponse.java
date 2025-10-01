package kr.co.readingtown.member.dto.response;

import java.util.List;

public record KeywordResponse(
        List<KeywordDetailResponse> moodKeyword,
        List<KeywordDetailResponse> genreKeyword,
        List<KeywordDetailResponse> contentKeyword
) {
}
