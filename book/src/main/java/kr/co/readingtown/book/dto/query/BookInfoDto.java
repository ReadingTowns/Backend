package kr.co.readingtown.book.dto.query;

public record BookInfoDto(
        String bookName,
        String bookImage,
        String author,
        String publisher,
        String summary
) {
}
