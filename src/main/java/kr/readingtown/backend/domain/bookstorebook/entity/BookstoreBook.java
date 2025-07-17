package kr.readingtown.backend.domain.bookstorebook.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.bookhouse.entity.Bookhouse;
import kr.readingtown.backend.domain.bookstore.entity.Bookstore;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookstoreBook extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookstoreBookId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookstore_id")
    private Bookstore bookstore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookhouse_id")
    private Bookhouse bookhouse;

    @Lob
    private String bookState;

    private String availableTime;

    private Boolean isExchanging;

    @Builder
    public BookstoreBook(Bookstore bookstore, Bookhouse bookhouse, String bookState, String availableTime, Boolean isExchanging) {
        this.bookstore = bookstore;
        this.bookhouse = bookhouse;
        this.bookState = bookState;
        this.availableTime = availableTime;
        this.isExchanging = isExchanging;
    }
}
