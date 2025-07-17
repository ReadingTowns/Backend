package kr.readingtown.backend.domain.category.entity;

import jakarta.persistence.*;
import kr.readingtown.backend.global.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;

    private String categoryName;

    /**
     * Constructs a new Category with the specified category name.
     *
     * @param categoryName the name of the category
     */
    @Builder
    public Category(String categoryName) {
        this.categoryName = categoryName;
    }
}
