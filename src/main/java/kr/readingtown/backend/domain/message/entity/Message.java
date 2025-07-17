package kr.readingtown.backend.domain.message.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.chatroom.entity.Chatroom;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.*;

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

    private LocalDateTime sentTime;

    /**
     * Constructs a Message entity with the specified chatroom, sender, content, and sent time.
     *
     * @param chatroom the chatroom to which this message belongs
     * @param sender the member who sent the message
     * @param content the content of the message
     * @param sentTime the timestamp when the message was sent
     */
    @Builder
    public Message(Chatroom chatroom, Member sender, String content, LocalDateTime sentTime) {
        this.chatroom = chatroom;
        this.sender = sender;
        this.content = content;
        this.sentTime = sentTime;
    }
}