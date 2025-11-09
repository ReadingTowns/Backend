package kr.co.readingtown.member.dto.response.internal;

public record ExchangingBookResponseDto(
        Long chatroomId,
        ExchangingBookDetail myBook,
        ExchangingBookDetail partnerBook
) {
    public record ExchangingBookDetail(
            Long bookhouseId,
            String bookName,
            String bookImage
    ) {
    }
}