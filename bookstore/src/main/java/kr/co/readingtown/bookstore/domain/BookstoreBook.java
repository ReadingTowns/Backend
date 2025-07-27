package kr.co.readingtown.bookstore.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bookstore_books")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookstoreBook extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookstore_book_id")
    private Long bookstoreBookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookstore_id")
    private Bookstore bookstore;

    @Column(name = "bookhouse_id")
    private Long bookhouseId;

    @Lob
    @Column(name = "book_state")
    private String bookState;

    @Column(name = "available_time")
    private String availableTime;

    @Column(name = "is_exchanging")
    private Boolean isExchanging;

    @Builder
    public BookstoreBook(Bookstore bookstore, Long bookhouseId, String bookState, String availableTime, Boolean isExchanging) {
        this.bookstore = bookstore;
        this.bookhouseId = bookhouseId;
        this.bookState = bookState;
        this.availableTime = availableTime;
        this.isExchanging = isExchanging;
    }
}
