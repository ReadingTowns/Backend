package kr.co.readingtown.social.repository;

import kr.co.readingtown.social.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Query("""
      select f.toFollowingId
      from Follow f
      where f.fromFollowerId = :fromId and f.toFollowingId in :targetIds
    """)
    Set<Long> findFollowingIdsIn(@Param("fromId") Long fromId, @Param("targetIds") List<Long> targetIds);

    boolean existsByFromFollowerIdAndToFollowingId(Long fromMemberId, Long toMemberId);

    void deleteByFromFollowerIdAndToFollowingId(Long fromMemberId, Long toMemberId);

    @Query("""
       select f.toFollowingId
       from Follow f
       where f.fromFollowerId = :memberId
       """)
    List<Long> findFollowingIds(Long memberId);

    @Query("""
       select f.fromFollowerId
       from Follow f
       where f.toFollowingId = :memberId
       """)
    List<Long> findFollowerIds(Long memberId);
}
