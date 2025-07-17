package kr.readingtown.backend.domain.review.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.book.entity.Book;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Lob
    private String content;

    @Builder
    public Review(Book book, Member member, String content) {
        this.book = book;
        this.member = member;
        this.content = content;
    }
}