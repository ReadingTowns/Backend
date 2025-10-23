package kr.co.readingtown.review.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.readingtown.review.dto.request.ReviewRequestDto;
import kr.co.readingtown.review.dto.response.ReviewResponseDto;
import kr.co.readingtown.review.dto.response.ReviewWithAuthorResponseDto;
import kr.co.readingtown.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@Tag(name = "Review API", description = "리뷰 관련 API")
public class ExternalReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{bookId}/review")
    @Operation(summary = "리뷰 작성", description = "특정 도서에 대한 리뷰를 작성합니다.")
    public ReviewResponseDto createReview(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId,
            @RequestBody ReviewRequestDto reviewCreateDto) {

        return reviewService.createReview(bookId, memberId, reviewCreateDto);
    }

    @PatchMapping("/review/{reviewId}")
    @Operation(summary = "리뷰 수정", description = "작성한 리뷰를 수정합니다.")
    public ReviewResponseDto updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal Long memberId,
            @RequestBody ReviewRequestDto reviewUpdateDto) {

        return reviewService.updateReview(reviewId, memberId, reviewUpdateDto);
    }

    @GetMapping("/{bookId}/reviews/me")
    @Operation(summary = "내 리뷰 조회", description = "특정 도서에 대한 내 리뷰를 조회합니다.")
    public ReviewResponseDto getMyReview(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId) {

        return reviewService.getReviewByMemberId(bookId, memberId);
    }

    @GetMapping("/{bookId}/reviews")
    @Operation(summary = "도서 리뷰 목록 조회", description = "특정 도서의 모든 리뷰를 조회합니다.")
    public List<ReviewWithAuthorResponseDto> getReviews(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId) {

        return reviewService.getReviews(bookId, memberId);
    }

    @GetMapping("/{bookId}/reviews/{memberId}")
    @Operation(summary = "특정 회원의 리뷰 조회", description = "특정 도서에 대한 특정 회원의 리뷰를 조회합니다.")
    public ReviewResponseDto getReviewByMemberId(
            @PathVariable Long bookId,
            @PathVariable Long memberId) {

        return reviewService.getReviewByMemberId(bookId, memberId);
    }
}
