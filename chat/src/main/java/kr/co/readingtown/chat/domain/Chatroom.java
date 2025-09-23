package kr.co.readingtown.chat.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.*;

import java.util.Objects;

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

    public void removeOwnerId() {
        this.ownerId = null;
    }

    public void removeRequesterId() {
        this.requesterId = null;
    }

    public boolean hasMember(Long memberId) {
        return Objects.equals(ownerId, memberId) || Objects.equals(requesterId, memberId);
    }

    public boolean isEmpty() {
        return ownerId == null && requesterId == null;
    }

    @Builder
    public Chatroom(Long ownerId, Long requesterId) {
        this.ownerId = ownerId;
        this.requesterId = requesterId;
    }
}