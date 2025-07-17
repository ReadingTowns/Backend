package kr.readingtown.backend.domain.message.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.chatroom.entity.Chatroom;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id")
    private Chatroom chatroom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @Lob
    private String content;

    @CreatedDate
    private LocalDateTime sentTime;

    @Builder
    public Message(Chatroom chatroom, Member sender, String content) {
        this.chatroom = chatroom;
        this.sender = sender;
        this.content = content;
    }
}