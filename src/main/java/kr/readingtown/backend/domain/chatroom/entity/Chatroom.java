package kr.readingtown.backend.domain.chatroom.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.exchangematch.entity.ExchangeMatch;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.domain.rental.entity.Rental;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id")
    private Member requester;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rental_id")
    private Rental rental;

    @Builder
    public Chatroom(Member owner, Member requester, Rental rental) {
        this.owner = owner;
        this.requester = requester;
        this.rental = rental;
    }
}