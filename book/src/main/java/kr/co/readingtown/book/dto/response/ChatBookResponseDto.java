package kr.co.readingtown.book.dto.response;

import kr.co.readingtown.book.dto.query.BookInfoDto;

public record ChatBookResponseDto(
        String bookName,
        String bookImage
) {

    public static ChatBookResponseDto toChatBookResponseDto(BookInfoDto bookInfoDto) {

        return new ChatBookResponseDto(
               bookInfoDto.bookName(),
               bookInfoDto.bookImage()
        );
    }
}
