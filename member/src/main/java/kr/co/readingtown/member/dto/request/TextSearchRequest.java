package kr.co.readingtown.member.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TextSearchRequest(
        String query,
        @JsonProperty("top_k")
        Integer topK,
        @JsonProperty("use_combined")
        Boolean useCombined
) {
    public TextSearchRequest(String query) {
        this(query, 10, true);
    }
}