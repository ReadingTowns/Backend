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

    @Builder
    public Book(String bookName, String bookImage, String author, String summary, String publisher) {
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.author = author;
        this.summary = summary;
        this.publisher = publisher;
    }
}
