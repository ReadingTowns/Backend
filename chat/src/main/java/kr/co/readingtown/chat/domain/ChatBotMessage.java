package kr.co.readingtown.chat.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseTimeEntity;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@Table(name = "chatbot_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatBotMessage extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Comment("사용자 ID")
    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Comment("메시지 역할 (USER, BOT)")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MessageRole role;

    @Comment("메시지 내용")
    @Lob
    @Column(nullable = false)
    private String content;

    @Builder
    public ChatBotMessage(Long memberId, MessageRole role, String content) {
        this.memberId = memberId;
        this.role = role;
        this.content = content;
    }
}