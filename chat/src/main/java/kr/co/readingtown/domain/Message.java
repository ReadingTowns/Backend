package kr.co.readingtown.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @Column(name = "sender_id")
    private Long senderId;

    @Lob
    private String content;

    @CreatedDate
    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Builder
    public Message(Chatroom chatroom, Long senderId, String content) {
        this.chatroom = chatroom;
        this.senderId = senderId;
        this.content = content;
    }
}