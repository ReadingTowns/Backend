package kr.readingtown.backend.domain.member.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    private String username;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    private String loginId;

    @Lob
    private String profileImage;

    private String phoneNumber;

    private Double userRating;

    private Integer userRatingCount;

    private String currentTown;

    private Double chatResponseRate;

    private Integer chatAvgResponseMinutes;

    /**
     * Constructs a new Member entity with the specified profile and account details.
     *
     * Initializes all member attributes except for the primary key, which is managed by the persistence layer.
     */
    @Builder
    public Member(String username, LoginType loginType, String loginId, String profileImage, String phoneNumber, Double userRating, Integer userRatingCount, String currentTown, Double chatResponseRate, Integer chatAvgResponseMinutes) {
        this.username = username;
        this.loginType = loginType;
        this.loginId = loginId;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.userRating = userRating;
        this.userRatingCount = userRatingCount;
        this.currentTown = currentTown;
        this.chatResponseRate = chatResponseRate;
        this.chatAvgResponseMinutes = chatAvgResponseMinutes;
    }
}
