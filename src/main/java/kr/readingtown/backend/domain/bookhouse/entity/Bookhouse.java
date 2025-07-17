package kr.readingtown.backend.domain.bookhouse.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.book.entity.Book;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookhouse extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookhouseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Builder
    public Bookhouse(Member member, Book book) {
        this.member = member;
        this.book = book;
    }
}
