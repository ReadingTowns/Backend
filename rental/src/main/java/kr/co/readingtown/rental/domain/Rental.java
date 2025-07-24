package kr.co.readingtown.rental.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import kr.co.readingtown.rental.domain.enums.ExchangeStatus;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "rentals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rental extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rental_id")
    private Long rentalId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "owner_bookstore_book_id")
    private Long ownerBookstoreBookId;

    @Column(name = "requester_bookstore_book_id")
    private Long requesterBookstoreBookId;

    @Enumerated(EnumType.STRING)
    @Column(name = "exchange_status")
    private ExchangeStatus exchangeStatus;

    @Builder
    public Rental(Long ownerId, Long requesterId, Long ownerBookstoreBookId,
                  Long requesterBookstoreBookId, ExchangeStatus exchangeStatus) {
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.ownerBookstoreBookId = ownerBookstoreBookId;
        this.requesterBookstoreBookId = requesterBookstoreBookId;
        this.exchangeStatus = exchangeStatus;
    }
}
