package kr.co.readingtown.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@Table(name = "chatrooms")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chatroom extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long chatroomId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "rental_id")
    private Long rentalId;

    @Builder
    public Chatroom(Long ownerId, Long requesterId, Long rentalId) {
        this.ownerId = ownerId;
        this.requesterId = requesterId;
        this.rentalId = rentalId;
    }
}