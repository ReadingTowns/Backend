package kr.readingtown.backend.domain.bookstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookstore extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookstoreId;

    private String townName;

    private String townAddress;

    /**
     * Constructs a new Bookstore entity with the specified town name and address.
     *
     * @param townName    the name of the town where the bookstore is located
     * @param townAddress the address of the bookstore within the town
     */
    @Builder
    public Bookstore(String townName, String townAddress) {
        this.townName = townName;
        this.townAddress = townAddress;
    }
}
