package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.MemberKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberKeywordRepository extends JpaRepository<MemberKeyword, Long> {

    @Query("""
    SELECT mk.keywordId
    FROM MemberKeyword mk
    WHERE mk.memberId = :memberId
    """)
    List<Long> findByMemberId(@Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM MemberKeyword mk WHERE mk.memberId = :memberId AND mk.keywordId IN :keywordIds")
    void deleteAllByMemberIdAndKeywordIds(@Param("memberId") Long memberId, @Param("keywordIds") List<Long> keywordIds);

    @Modifying
    @Query("""
    DELETE
    FROM MemberKeyword mk
    WHERE mk.memberId = :memberId
    """)
    void deleteAllByMemberId(@Param("memberId") Long memberId);
}
