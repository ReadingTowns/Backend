package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    @Query("""
    SELECT k.content
    FROM Keyword k
        JOIN MemberKeyword mk ON mk.keywordId = k.keywordId
    WHERE mk.memberId = :memberId
    """)
    List<String> findContentsByMemberId(@Param("memberId") Long memberId);
    
    @Query("SELECT COUNT(k) FROM Keyword k WHERE k.keywordId IN :ids")
    long countByIdIn(@Param("ids") List<Long> ids);
}
