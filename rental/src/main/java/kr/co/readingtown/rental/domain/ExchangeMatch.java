package kr.co.readingtown.rental.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import kr.co.readingtown.rental.domain.enums.RequestStatus;
import lombok.*;

@Entity
@Getter
@Table(name = "exchange_matches")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeMatch extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exchange_match_id")
    private Long exchangeMatchId;

    @Column(name = "chat_room_id")
    private Long chatroomId;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    @Builder
    public ExchangeMatch(Long chatroomId, Long requesterId, Long receiverId, RequestStatus requestStatus) {
        this.chatroomId = chatroomId;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.requestStatus = requestStatus;
    }

    // 상태 변경 메서드
    public void updateStatus(RequestStatus newStatus) {
        this.requestStatus = newStatus;
    }

    public void accept() {
        updateStatus(RequestStatus.ACCEPTED);
    }

    public void reject() {
        updateStatus(RequestStatus.REJECTED);
    }
}
