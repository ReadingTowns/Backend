package kr.co.readingtown.book.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "books")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "book_name")
    private String bookName;

    @Column(name = "book_image")
    private String bookImage;

    private String author;

    @Lob
    private String summary;

    private String publisher;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Lob
    @Column(name = "keyword")
    private String keyword;

    @Lob
    private String review;

    @Column(name = "source_field")
    @Enumerated(EnumType.STRING)
    private SourceFieldType sourceField;

    @Builder
    public Book(String bookName, String bookImage, String author, String summary, String publisher, String isbn, String keyword, String review, SourceFieldType sourceField) {
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.author = author;
        this.summary = summary;
        this.publisher = publisher;
        this.isbn = isbn;
        this.keyword = keyword;
        this.review = review;
        this.sourceField = sourceField;
    }
}
