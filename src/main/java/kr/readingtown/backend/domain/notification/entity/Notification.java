package kr.readingtown.backend.domain.notification.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.domain.member.entity.Member;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    // 알림을 받는 대상 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Lob
    private String content;

    @Builder
    public Notification(Member member, NotificationType type, String content) {
        this.member = member;
        this.type = type;
        this.content = content;
    }
}