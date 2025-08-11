package kr.co.readingtown.book.dto.response;

import kr.co.readingtown.book.dto.query.BookPreviewDto;

public record BookPreviewResponseDto(
        Long id,
        String image,
        String title,
        String authorName
) {

    public static BookPreviewResponseDto toBookPreviewResponseDto(BookPreviewDto bookPreviewDto) {

        return new BookPreviewResponseDto(
                bookPreviewDto.id(),
                bookPreviewDto.image(),
                bookPreviewDto.title(),
                bookPreviewDto.authorName()
        );
    }
}
