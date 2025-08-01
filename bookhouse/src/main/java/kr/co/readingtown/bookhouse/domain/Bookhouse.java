package kr.co.readingtown.bookhouse.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "bookhouses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookhouse extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookhouse_id")
    private Long bookhouseId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "book_id")
    private Long bookId;

    @Builder
    public Bookhouse(Long memberId, Long bookId) {
        this.memberId = memberId;
        this.bookId = bookId;
    }
}
