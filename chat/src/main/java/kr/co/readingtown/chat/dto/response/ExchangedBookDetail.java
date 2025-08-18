package kr.co.readingtown.chat.dto.response;

public record ExchangedBookDetail(
        Long exchangeStatusId,
        Long bookhouseId,
        String bookName,
        String bookImage,
        String isAccepted
) {
}
