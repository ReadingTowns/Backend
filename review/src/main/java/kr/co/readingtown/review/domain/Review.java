package kr.co.readingtown.review.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "book_id")
    private Long bookId;

    @Column(name = "member_id")
    private Long memberId;

    @Lob
    private String content;

    public void updateContent(String content) {
        this.content = content;
    }

    @Builder
    public Review(Long bookId, Long memberId, String content) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.content = content;
    }
}