package kr.co.readingtown.member.dto.response.ai;

import java.util.List;

public record BertSearchResponseDto(
        String query,
        List<BookSearchResponseDto> results
) {
}