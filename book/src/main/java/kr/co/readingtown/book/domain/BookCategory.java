package kr.co.readingtown.book.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "book_categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookCategory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_category_id")
    private Long bookCategoryId;

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
