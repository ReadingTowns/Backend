package kr.co.readingtown.member.integration.bookhouse;

import kr.co.readingtown.member.dto.response.internal.ExchangingBookResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookhouseReader {
    
    private final BookhouseClient bookhouseClient;
    
    public List<ExchangingBookResponseDto> getExchangingBooks(Long memberId) {

        List<ExchangingBookResponseDto> exchangingBookResponses = bookhouseClient.getExchangingBooks(memberId);

        if (exchangingBookResponses.isEmpty()) {
            return Collections.emptyList();
        }
        return exchangingBookResponses;
    }
}