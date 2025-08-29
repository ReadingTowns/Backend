package kr.co.readingtown.member.integration.bookhouse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookhouseReader {
    
    private final BookhouseClient bookhouseClient;
    
    public List<ExchangingBookResponse> getExchangingBooks(Long memberId) {

        List<ExchangingBookResponse> exchangingBookResponses = bookhouseClient.getExchangingBooks(memberId);

        if (exchangingBookResponses.isEmpty()) {
            return Collections.emptyList();
        }
        return exchangingBookResponses;
    }
}