package kr.co.readingtown.member.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import kr.co.readingtown.common.entity.BaseEntity;
import kr.co.readingtown.member.domain.enums.LoginType;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    private String username;

    @Column(name = "nickname", length = 20, unique = true)
    private String nickname;

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

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude; //경도

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude; //위도

    @Column(name = "chat_response_rate")
    private Double chatResponseRate;

    @Column(name = "chat_avg_response_minutes")
    private Integer chatAvgResponseMinutes;

    @Column(name = "available_time")
    private String availableTime;

    @Column(name = "is_onboarded")
    private boolean isOnboarded = false;

    public void updateImage(String image) {
        this.profileImage = image;
    }

    public void updateProfile(String nickname, String availableTime) {
        this.nickname = nickname;
        this.availableTime = availableTime;
    }

    public void addStarRating(int starRating) {
        this.userRatingSum += starRating;
        this.userRatingCount += 1;
        this.userRating = (double)this.userRatingSum / (double)this.userRatingCount;
    }

    @Builder
    public Member(String username, LoginType loginType, String loginId, String nickname, String profileImage, String phoneNumber, Integer userRatingSum, Integer userRatingCount, Double userRating, String currentTown, BigDecimal longitude, BigDecimal latitude, Double chatResponseRate, Integer chatAvgResponseMinutes, String availableTime, boolean isOnboarded) {

        this.username = username;
        this.nickname = nickname;
        this.loginType = loginType;
        this.loginId = loginId;
        this.profileImage = profileImage;
        this.phoneNumber = phoneNumber;
        this.userRating = userRating;
        this.userRatingSum = userRatingSum;
        this.userRatingCount = userRatingCount;
        this.currentTown = currentTown;
        this.longitude = longitude;
        this.latitude = latitude;
        this.chatResponseRate = chatResponseRate;
        this.chatAvgResponseMinutes = chatAvgResponseMinutes;
        this.availableTime = availableTime;
        this.isOnboarded = isOnboarded;
    }

    public void completeOnboarding(String phoneNumber, String currentTown, BigDecimal longitude, BigDecimal latitude, String nickname, String profileImage, String availableTime) {
        this.phoneNumber = phoneNumber;
        this.currentTown = currentTown;
        this.longitude = longitude;
        this.latitude = latitude;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.availableTime = availableTime;
        this.isOnboarded = true;
    }

    public void updateTown(BigDecimal longitude, BigDecimal latitude, String currentTown) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.currentTown = currentTown;
    }
}
