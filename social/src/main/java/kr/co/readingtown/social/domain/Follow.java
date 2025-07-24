package kr.co.readingtown.social.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.*;

@Entity
@Getter
@Table(name = "follows")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Long followId;

    // follower
    @Column(name = "from_follower_id")
    private Long fromFollowerId;

    // following
    @Column(name = "to_following_id")
    private Long toFollowingId;

    @Builder
    public Follow(Long fromFollowerId, Long toFollowingId) {
        this.fromFollowerId = fromFollowerId;
        this.toFollowingId = toFollowingId;
    }
}