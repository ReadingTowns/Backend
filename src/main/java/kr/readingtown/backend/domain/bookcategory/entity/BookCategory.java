package kr.readingtown.backend.domain.bookcategory.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.book.entity.Book;
import kr.readingtown.backend.domain.category.entity.Category;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookCategory extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long BookCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public BookCategory(Book book, Category category) {
        this.book = book;
        this.category = category;
    }

}
