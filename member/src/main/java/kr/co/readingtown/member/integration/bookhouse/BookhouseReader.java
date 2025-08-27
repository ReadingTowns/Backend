package kr.co.readingtown.member.integration.bookhouse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookhouseReader {
    
    private final BookhouseClient bookhouseClient;
    
    public List<ExchangingBookResponse> getExchangingBooks(Long memberId) {
        return bookhouseClient.getExchangingBooks(memberId);
    }
}