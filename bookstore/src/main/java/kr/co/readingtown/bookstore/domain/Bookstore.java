package kr.co.readingtown.bookstore.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bookstores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookstore extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookstore_id")
    private Long bookstoreId;

    @Column(name = "town_name")
    private String townName;

    @Column(name = "town_address")
    private String townAddress;

    @Builder
    public Bookstore(String townName, String townAddress) {
        this.townName = townName;
        this.townAddress = townAddress;
    }
}
