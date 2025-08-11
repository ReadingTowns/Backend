package kr.co.readingtown.common.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int curPage,   // 현재 페이지 번호
        int curElements,   // 현재 페이지에서의 데이터 개수
        int totalPages,
        long totalElements,
        boolean last
) {
    public static <T> PageResponse<T> of(List<T> content, Page<?> page) {
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getNumberOfElements(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast()
        );
    }
}