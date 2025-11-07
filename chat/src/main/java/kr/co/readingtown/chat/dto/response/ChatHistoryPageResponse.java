package kr.co.readingtown.chat.dto.response;

import java.util.List;

public record ChatHistoryPageResponse(
        List<ChatBotResponse> messages,
        Boolean hasMore,
        Long nextBeforeId
) {
}
