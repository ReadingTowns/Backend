package kr.co.readingtown.bookhouse.repository;

import jakarta.persistence.LockModeType;
import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookhouseRepository extends JpaRepository<Bookhouse, Long> {

    Page<Bookhouse> findAllByMemberId(Long memberId, Pageable pageable);

    Optional<Bookhouse> findByMemberIdAndBookId(Long memberId, Long bookId);

    boolean existsByMemberIdAndBookId(Long memberId, Long bookId);

    List<Bookhouse> findByMemberIdAndIsExchanged(Long memberId, IsExchanged isExchanged);

    List<Bookhouse> findAllByChatroomId(Long chatroomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Bookhouse b where b.bookhouseId in :bookhouseIds")
    List<Bookhouse> findAllByIdForUpdate(@Param("bookhouseIds")List<Long> bookhouseIds);
}
