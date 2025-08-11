package kr.co.readingtown.book.dto.request;

import kr.co.readingtown.book.domain.Book;
import kr.co.readingtown.book.domain.SourceFieldType;

public record BookInfoRequestDto(
        String isbn,
        String image,
        String title,
        String author,
        String publisher
) {

    public Book toBookEntity() {

        return Book.builder()
                .isbn(isbn)
                .bookImage(image)
                .bookName(title)
                .author(author)
                .publisher(publisher)
                .sourceField(SourceFieldType.MANUAL)
                .build();
    }
}
