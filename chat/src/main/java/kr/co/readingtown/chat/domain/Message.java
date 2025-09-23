package kr.co.readingtown.chat.domain;

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

    @Column(name = "chatroom_id")
    private Long chatroomId;

    @Column(name = "sender_id")
    private Long senderId;

    @Lob
    private String content;

    @CreatedDate
    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Builder
    public Message(Long chatroomId, Long senderId, String content) {
        this.chatroomId = chatroomId;
        this.senderId = senderId;
        this.content = content;
    }
}