package kr.readingtown.backend.domain.rental.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.bookstorebook.entity.BookstoreBook;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rentalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_bookstore_book_id")
    private BookstoreBook ownerBookstoreBook;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_bookstore_book_id")
    private BookstoreBook requesterBookstoreBook;

    @Enumerated(EnumType.STRING)
    private ExchangeStatus exchangeStatus;

    /**
     * Constructs a Rental entity representing a book exchange transaction between two members.
     *
     * @param owner the member who owns the book being offered for rental
     * @param requester the member requesting the rental
     * @param ownerBookstoreBook the book owned by the owner involved in the rental
     * @param requesterBookstoreBook the book owned by the requester involved in the rental
     * @param exchangeStatus the current status of the exchange
     */
    @Builder
    public Rental(Member owner, Member requester, BookstoreBook ownerBookstoreBook,
                  BookstoreBook requesterBookstoreBook, ExchangeStatus exchangeStatus) {
        this.owner = owner;
        this.requester = requester;
        this.ownerBookstoreBook = ownerBookstoreBook;
        this.requesterBookstoreBook = requesterBookstoreBook;
        this.exchangeStatus = exchangeStatus;
    }
}
