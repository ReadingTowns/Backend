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

    @Builder
    public Book(String bookName, String bookImage, String author, String summary, String publisher) {
        this.bookName = bookName;
        this.bookImage = bookImage;
        this.author = author;
        this.summary = summary;
        this.publisher = publisher;
    }
}
