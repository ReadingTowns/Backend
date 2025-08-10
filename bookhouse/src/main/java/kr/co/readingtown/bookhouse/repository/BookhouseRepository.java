package kr.co.readingtown.bookhouse.repository;


import kr.co.readingtown.bookhouse.domain.Bookhouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookhouseRepository extends JpaRepository<Bookhouse, Long> {

     boolean existsByMemberId(Long memberId);

     @Query("SELECT b.bookhouseId FROM Bookhouse b WHERE b.memberId = :memberId")
     Optional<Long> findBookhouseIdByMemberId(@Param("memberId") Long memberId);
}