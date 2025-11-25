package kr.co.readingtown.review.internalapi;

import kr.co.readingtown.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/reviews")
public class InternalReviewController {

    private final ReviewService reviewService;

    @DeleteMapping("/revoke")
    public void deleteMembersReviews(@RequestParam("memberId") Long memberId) {

        reviewService.deleteReview(memberId);
    }
}
