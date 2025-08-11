package kr.co.readingtown.bookhouse.repository;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookhouseRepository extends JpaRepository<Bookhouse, Long> {

    Page<Bookhouse> findAllByMemberId(Long memberId, Pageable pageable);

    Optional<Bookhouse> findByMemberIdAndBookId(Long memberId, Long bookId);

    boolean existsByMemberIdAndBookId(Long memberId, Long bookId);
}
