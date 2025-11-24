package kr.co.readingtown.review.service;

import kr.co.readingtown.review.integration.book.BookValidator;
import kr.co.readingtown.review.domain.Review;
import kr.co.readingtown.review.dto.query.ReviewContentAndAuthorNameDto;
import kr.co.readingtown.review.dto.query.ReviewIdAndContentDto;
import kr.co.readingtown.review.dto.request.ReviewRequestDto;
import kr.co.readingtown.review.dto.response.ReviewResponseDto;
import kr.co.readingtown.review.dto.response.ReviewWithAuthorResponseDto;
import kr.co.readingtown.review.exception.ReviewException;
import kr.co.readingtown.review.integration.member.MemberReader;
import kr.co.readingtown.review.integration.member.MemberValidator;
import kr.co.readingtown.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final MemberReader memberReader;
    private final BookValidator bookValidator;
    private final MemberValidator memberValidator;
    private final ReviewRepository reviewRepository;

    // 책 감상평 등록
    @Transactional
    public ReviewResponseDto createReview(Long bookId, Long memberId, ReviewRequestDto reviewCreateDto) {

        memberValidator.validateMemberExists(memberId);
        bookValidator.validateBookExists(bookId);
        if (reviewRepository.existsByBookIdAndMemberId(bookId, memberId)) {
            throw new ReviewException.ReviewAlreadyExists();
        }

        Review newReview = Review.builder()
                .bookId(bookId)
                .memberId(memberId)
                .content(reviewCreateDto.content())
                .build();
        Review saved = reviewRepository.save(newReview);

        return ReviewResponseDto.toReviewResponseDto(saved);
    }

    // 책 감상평 수정
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, Long memberId, ReviewRequestDto reviewUpdateDto) {

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewException.ReviewNotFound::new);
        if (!review.getMemberId().equals(memberId))
            throw new ReviewException.ReviewAuthorMismatch();

        review.updateContent(reviewUpdateDto.content());
        Review saved = reviewRepository.save(review);

        return ReviewResponseDto.toReviewResponseDto(saved);
    }

    // 특정 책에 대한 특정 회원의 감상평 조회
    public ReviewResponseDto getReviewByMemberId(Long bookId, Long memberId) {

        bookValidator.validateBookExists(bookId);
        memberValidator.validateMemberExists(memberId);

        return reviewRepository.findReviewByBookIdAndMemberId(bookId, memberId)
                .map(ReviewResponseDto::toReviewResponseDto)
                .orElse(null);
    }

    // 특정 책의 모든 감상평 조회 (자기 자신 제외)
    public List<ReviewWithAuthorResponseDto> getReviews(Long bookId, Long memberId) {

        bookValidator.validateBookExists(bookId);
        List<ReviewContentAndAuthorNameDto> reviews = reviewRepository.findReviewByBookIdExcludingMe(bookId, memberId);
        if (reviews.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, String> memberNames = getMemberNamesForReviews(reviews);
        return convertToReviewWithAuthorResponseDto(reviews, memberNames);
    }

    private Map<Long, String> getMemberNamesForReviews(List<ReviewContentAndAuthorNameDto> reviews) {

        List<Long> memberIds = reviews.stream()
                .map(ReviewContentAndAuthorNameDto::memberId)
                .toList();

        return memberReader.getMemberNames(memberIds);
    }

    private List<ReviewWithAuthorResponseDto> convertToReviewWithAuthorResponseDto(
            List<ReviewContentAndAuthorNameDto> reviews, Map<Long, String> memberNames) {

        return reviews.stream()
                .map(review -> new ReviewWithAuthorResponseDto(memberNames.get(review.memberId()), review.content()))
                .toList();
    }

    @Transactional
    public void deleteReview(Long memberId) {

        reviewRepository.deleteReviewByMemberId(memberId);
    }
}
