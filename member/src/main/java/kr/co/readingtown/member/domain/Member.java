package kr.co.readingtown.member.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import kr.co.readingtown.member.domain.enums.LoginType;
import lombok.*;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private LoginType loginType;

    @Column(name = "login_id")
    private String loginId;

    @Lob
    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_rating_sum") //리뷰 점수 합
    private Integer userRatingSum = 0;

    @Column(name = "user_rating_count") //리뷰 개수
    private Integer userRatingCount = 0;

    @Column(name = "user_rating") //리뷰 평점
    private Double userRating;

    @Column(name = "current_town")
    private String currentTown;

    @Column(name = "chat_response_rate")
    private Double chatResponseRate;

    @Column(name = "chat_avg_response_minutes")
    private Integer chatAvgResponseMinutes;

    @Column(name = "available_time")
    private String availableTime;

    public void updateProfile(String username, String profileImage, String availableTime) {
        this.username = username;
        this.profileImage = profileImage;
        this.availableTime = availableTime;
    }

    public void addStarRating(int starRating) {
        this.userRatingSum += starRating;
        this.userRatingCount += 1;
        this.userRating = (double)this.userRatingSum / (double)this.userRatingCount;
    }

    @Builder
    public Member(String username, LoginType loginType, String loginId, String profileImage, String phoneNumber, Integer userRatingSum, Integer userRatingCount, Double userRating, String currentTown, Double chatResponseRate, Integer chatAvgResponseMinutes, String availableTime) {
        this.username = username;
        this.loginType = loginType;
        this.loginId = loginId;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.userRating = userRating;
        this.userRatingSum = userRatingSum;
        this.userRatingCount = userRatingCount;
        this.currentTown = currentTown;
        this.chatResponseRate = chatResponseRate;
        this.chatAvgResponseMinutes = chatAvgResponseMinutes;
        this.availableTime = availableTime;
    }

    public void completeOnboarding(String phoneNumber, String currentTown, String username, String profileImage, String availableTime) {
        this.phoneNumber = phoneNumber;
        this.currentTown = currentTown;
        this.username = username;
        this.profileImage = profileImage;
        this.availableTime = availableTime;
    }
}
