package kr.co.readingtown.bookhouse.dto.request;

public record BookInfoRequestDto(
        String isbn,
        String image,
        String title,
        String author,
        String publisher
) {
}
