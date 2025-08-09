package kr.co.readingtown.review.externalapi;

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
public class ExternalReviewController {

    private final ReviewService reviewService;

    @PostMapping("/{bookId}/review")
    public ReviewResponseDto createReview(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId,
            @RequestBody ReviewRequestDto reviewCreateDto) {

        return reviewService.createReview(bookId, memberId, reviewCreateDto);
    }

    @PatchMapping("/review/{reviewId}")
    public ReviewResponseDto updateReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequestDto reviewUpdateDto) {

        return reviewService.updateReview(reviewId, reviewUpdateDto);
    }

    @GetMapping("/{bookId}/reviews/me")
    public ReviewResponseDto getMyReview(
            @PathVariable Long bookId,
            @AuthenticationPrincipal Long memberId) {

        return reviewService.getReviewByMemberId(bookId, memberId);
    }

    @GetMapping("/{bookId}/reviews")
    public List<ReviewWithAuthorResponseDto> getReviews(@PathVariable Long bookId) {

        return reviewService.getReviews(bookId);
    }

    @GetMapping("/{bookId}/reviews/{memberId}")
    public ReviewResponseDto getReviewByMemberId(
            @PathVariable Long bookId,
            @PathVariable Long memberId) {

        return reviewService.getReviewByMemberId(bookId, memberId);
    }
}
