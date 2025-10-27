package kr.co.readingtown.member.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "member_keywords",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_id", "keyword_id"})
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_keyword_id")
    private Long memberKeywordId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "keyword_id")
    private Long keywordId;

    @Builder
    public MemberKeyword(Long memberId, Long keywordId) {
        this.memberId = memberId;
        this.keywordId = keywordId;
    }
}
