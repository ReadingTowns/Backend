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

    /**
     * Constructs an ExchangeMatch entity with the specified chatroom, requester, receiver, and request status.
     *
     * @param chatroom the chatroom associated with this exchange match
     * @param requester the member initiating the exchange request
     * @param receiver the member receiving the exchange request
     * @param requestStatus the initial status of the exchange request
     */
    @Builder
    public ExchangeMatch(Chatroom chatroom, Member requester, Member receiver, RequestStatus requestStatus) {
        this.chatroom = chatroom;
        this.requester = requester;
        this.receiver = receiver;
        this.requestStatus = requestStatus;
    }

    /**
     * Updates the status of the exchange request to the specified value.
     *
     * @param newStatus the new status to set for the exchange request
     */
    public void updateStatus(RequestStatus newStatus) {
        this.requestStatus = newStatus;
    }

    /**
     * Sets the request status of this exchange match to {@code ACCEPTED}.
     */
    public void accept() {
        updateStatus(RequestStatus.ACCEPTED);
    }

    /**
     * Sets the request status of this exchange match to REJECTED.
     */
    public void reject() {
        updateStatus(RequestStatus.REJECTED);
    }
}
