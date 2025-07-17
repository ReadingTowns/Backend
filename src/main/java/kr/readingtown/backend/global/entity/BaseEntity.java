package kr.readingtown.backend.global.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity extends BaseTimeEntity {

    private LocalDateTime deletedAt;

    /**
     * Marks the entity as soft deleted by setting the deletion timestamp to the current time.
     */
    public void markAsDeleted() { //soft 삭제로 스탬프 저장
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Checks whether the entity has been soft deleted.
     *
     * @return {@code true} if the entity is marked as deleted; {@code false} otherwise
     */
    public boolean isDeleted() { //soft 삭제 여부
        return this.deletedAt != null;
    }
}
