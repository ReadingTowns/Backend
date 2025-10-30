package kr.co.readingtown.chat.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Comment;
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

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    @Comment("교환 신청 대상 책 ID")
    @Column(name = "related_bookhouse_id")
    private Long relatedBookhouseId;

    @Comment("교환 상태 객체 ID : 이 메시지가 어떤 교환 요청(ExchangeStatus)과 연결된 것인지")
    @Column(name = "related_exchange_status_id")
    private Long relatedExchangeStatusId;

    @Lob
    private String content;

    @CreatedDate
    @Column(name = "sent_time")
    private LocalDateTime sentTime;

    @Builder
    public Message(Long chatroomId, Long senderId, String content, MessageType messageType, Long relatedBookhouseId, Long relatedExchangeStatusId) {
        this.chatroomId = chatroomId;
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
        this.relatedBookhouseId = relatedBookhouseId;
        this.relatedExchangeStatusId = relatedExchangeStatusId;
    }
}