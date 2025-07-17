package kr.readingtown.backend.domain.book.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    private String bookName;

    private String bookImage;

    private String author;

    @Lob
    private String summary;

    private String publisher;

    /**
     * Constructs a new Book entity with the specified details.
     *
     * @param bookName   the title of the book
     * @param bookImage  the URL or path to the book's image
     * @param author     the author of the book
     * @param summary    a summary or description of the book
     * @param publisher  the publisher of the book
     */
    @Builder
    public Book(String bookName, String bookImage, String author, String summary, String publisher) {
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.author = author;
        this.summary = summary;
        this.publisher = publisher;
    }
}
