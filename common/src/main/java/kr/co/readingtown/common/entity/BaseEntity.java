package kr.co.readingtown.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void markAsDeleted() { //soft 삭제로 스탬프 저장
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() { //soft 삭제 여부
        return this.deletedAt != null;
    }
}
