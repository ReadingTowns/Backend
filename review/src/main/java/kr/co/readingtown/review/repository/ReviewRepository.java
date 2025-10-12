package kr.co.readingtown.review.repository;

import kr.co.readingtown.review.domain.Review;
import kr.co.readingtown.review.dto.query.ReviewContentAndAuthorNameDto;
import kr.co.readingtown.review.dto.query.ReviewIdAndContentDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByBookIdAndMemberId(Long bookId, Long memberId);

    @Query("""
    SELECT new kr.co.readingtown.review.dto.query.ReviewIdAndContentDto(
        rv.reviewId,
        rv.content
    )
    FROM Review rv
    WHERE rv.bookId = :bookId
        AND rv.memberId = :memberId
    """)
    Optional<ReviewIdAndContentDto> findReviewByBookIdAndMemberId(@Param("bookId") Long bookId, @Param("memberId") Long memberId);

    @Query("""
    SELECT new kr.co.readingtown.review.dto.query.ReviewContentAndAuthorNameDto(
        rv.memberId,
        rv.content
    )
    FROM Review rv
    WHERE rv.bookId = :bookId
    """)
    List<ReviewContentAndAuthorNameDto> findReviewByBookId(@Param("bookId") Long bookId);

    @Query("""
    SELECT new kr.co.readingtown.review.dto.query.ReviewContentAndAuthorNameDto(
        rv.memberId,
        rv.content
    )
    FROM Review rv
    WHERE rv.bookId = :bookId
        AND rv.memberId <> :memberId
    """)
    List<ReviewContentAndAuthorNameDto> findReviewByBookIdExcludingMe(@Param("bookId") Long bookId, @Param("memberId") Long memberId);
}
