package kr.co.readingtown.book.dto.response;

import kr.co.readingtown.book.dto.query.BookInfoDto;

public record BookResponseDto(
        String bookName,
        String bookImage,
        String author,
        String publisher,
        String summary
) {

    public static BookResponseDto toBookResponseDto(BookInfoDto dto) {

        return new BookResponseDto(
                dto.bookName(),
                dto.bookImage(),
                dto.author(),
                dto.publisher(),
                dto.summary()
        );
    }
}
