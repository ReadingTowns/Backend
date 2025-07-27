package kr.co.readingtown.member.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_bookstores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberBookstore extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_bookstore_id")
    private Long memberBookstoreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "bookstore_id")
    private Long bookstoreId;

    @Builder
    public MemberBookstore(Member member, Long bookstoreId) {
        this.member = member;
        this.bookstoreId = bookstoreId;
    }
}
