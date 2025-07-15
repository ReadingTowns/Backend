package kr.readingtown.backend.global.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {

    private LocalDateTime deletedAt;

    public void markAsDeleted() { //soft 삭제로 스탬프 저장
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() { //soft 삭제 여부
        return this.deletedAt != null;
    }
}
