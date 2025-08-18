package kr.co.readingtown.bookhouse.dto.response;

public record ExchangedBookResponse(
        ExchangedDetail myBook,    // 내가 빌려 줄
        ExchangedDetail partnerBook   // 내가 받을
) {
}
