package kr.co.readingtown.bookhouse.domain;

import jakarta.persistence.*;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "is_exchanged", nullable = false)
    private IsExchanged isExchanged;

    @Column(name = "chatroom_id")
    private Long chatroomId;

    @Builder
    public Bookhouse(Long memberId, Long bookId, IsExchanged isExchanged, Long chatroomId) {
        this.memberId = memberId;
        this.bookId = bookId;
        this.isExchanged = isExchanged;
        this.chatroomId = chatroomId;
        this.isExchanged = IsExchanged.PENDING;
    }

    public void updateIsExchanged(IsExchanged isExchanged) {
        this.isExchanged = isExchanged;
    }

    public void updateChatroomId(Long chatroomId) {
        this.chatroomId = chatroomId;
    }

}
