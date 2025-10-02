package kr.co.readingtown.member.domain;

import jakarta.persistence.*;
import kr.co.readingtown.common.entity.BaseEntity;
import kr.co.readingtown.member.domain.enums.KeywordType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "keywords")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long keywordId;

    private String content;

    @Enumerated(EnumType.STRING)
    private KeywordType type;

    @Builder
    public Keyword(String content, KeywordType type) {
        this.content = content;
        this.type = type;
    }
}
