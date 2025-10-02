package kr.co.readingtown.member.dto.request;

import java.util.List;

public record KeywordRequest(
        List<Long> keywordIds
) {
}
