package kr.readingtown.backend.domain.memberbookstore.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.bookstore.entity.Bookstore;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBookstore extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberBookstoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookstore_id")
    private Bookstore bookstore;

    /**
     * Constructs a MemberBookstore entity linking the specified member and bookstore.
     *
     * @param member    the member associated with this entity
     * @param bookstore the bookstore associated with this entity
     */
    @Builder
    public MemberBookstore(Member member, Bookstore bookstore) {
        this.member = member;
        this.bookstore = bookstore;
    }
}
