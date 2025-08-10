package kr.co.readingtown.bookhouse.repository;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookhouseRepository extends JpaRepository<Bookhouse, Long> {

    List<Bookhouse> findAllByMemberId(Long memberId);

    Optional<Bookhouse> findByMemberIdAndBookId(Long memberId, Long bookId);

    boolean existsByMemberIdAndBookId(Long memberId, Long bookId);
}
