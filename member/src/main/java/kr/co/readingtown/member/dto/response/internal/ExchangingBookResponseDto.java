package kr.co.readingtown.member.dto.response.internal;

public record ExchangingBookResponseDto(
        Long chatRoomId,
        ExchangingBookDetail myBook,
        ExchangingBookDetail yourBook
) {
    public record ExchangingBookDetail(
            Long bookhouseId,
            String bookName,
            String bookImage
    ) {
    }
}