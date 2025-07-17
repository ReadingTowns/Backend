package kr.readingtown.backend.domain.exchangematch.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.chatroom.entity.Chatroom;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExchangeMatch extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exchangeMatchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private Chatroom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Builder
    public ExchangeMatch(Chatroom chatroom, Member requester, Member receiver, RequestStatus requestStatus) {
        this.chatroom = chatroom;
        this.requester = requester;
        this.receiver = receiver;
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
