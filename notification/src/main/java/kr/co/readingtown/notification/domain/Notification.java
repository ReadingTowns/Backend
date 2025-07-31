package kr.co.readingtown.notification.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import kr.co.readingtown.notification.domain.enums.NotificationType;
import lombok.*;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    // 알림을 받는 대상 회원
    @Column(name = "member_id")
    private Long memberId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Lob
    private String content;

    @Builder
    public Notification(Long memberId, NotificationType type, String content) {
        this.memberId = memberId;
        this.type = type;
        this.content = content;
    }
}