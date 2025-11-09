package kr.co.readingtown.bookhouse.repository;

import jakarta.persistence.LockModeType;
import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.dto.response.BookhouseSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookhouseRepository extends JpaRepository<Bookhouse, Long> {

    Page<Bookhouse> findAllByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    Optional<Bookhouse> findByMemberIdAndBookId(Long memberId, Long bookId);

    boolean existsByMemberIdAndBookId(Long memberId, Long bookId);

    List<Bookhouse> findByMemberIdAndIsExchangedOrderByCreatedAtDesc(Long memberId, IsExchanged isExchanged);

    List<Bookhouse> findAllByChatroomId(Long chatroomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Bookhouse b WHERE b.chatroomId = :chatroomId")
    List<Bookhouse> findAllByChatroomIdForUpdate(@Param("chatroomId") Long chatroomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Bookhouse b where b.bookhouseId in :bookhouseIds")
    List<Bookhouse> findAllByIdForUpdate(@Param("bookhouseIds")List<Long> bookhouseIds);

    @Query("""
    SELECT new kr.co.readingtown.bookhouse.dto.response.BookhouseSearchResponseDto(
        bh.bookhouseId,
        bh.bookId,
        b.bookName,
        b.bookImage,
        b.author
    )
    FROM Bookhouse bh
    JOIN Book b ON bh.bookId = b.bookId
    WHERE (REPLACE(LOWER(b.bookName), ' ', '') LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR REPLACE(LOWER(b.author), ' ', '') LIKE LOWER(CONCAT('%', :keyword, '%'))
       OR REPLACE(LOWER(b.publisher), ' ', '') LIKE LOWER(CONCAT('%', :keyword, '%')))
       AND (:excludeMemberId IS NULL OR bh.memberId != :excludeMemberId)
    ORDER BY bh.createdAt DESC
    """)
    List<BookhouseSearchResponseDto> searchBooksInBookhouse(@Param("keyword") String keyword,
                                                            @Param("excludeMemberId") Long excludeMemberId);

    List<Bookhouse> findAllByBookIdOrderByCreatedAtDesc(Long bookId);

    @Query("""
    SELECT bh.bookId
    FROM Bookhouse bh
    WHERE bh.memberId = :memberId
    """)
    List<Long> findBookIdByMember(@Param("memberId") Long memberId);
}
