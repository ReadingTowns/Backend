package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import kr.co.readingtown.member.dto.query.MemberIdNameDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginTypeAndLoginId(LoginType loginType, String loginId);

    Member findByLoginTypeAndLoginId(LoginType loginType, String loginId);
  
    boolean existsByNickname(String candidate);

    //유저 검색
    List<Member> findByNicknameContainingIgnoreCase(String nickname);

    boolean existsByUsername(String candidate);

    @Query("""
    SELECT new kr.co.readingtown.member.dto.query.MemberIdNameDto(
        m.memberId,
        m.nickname
    )
    FROM Member m
    WHERE m.memberId IN :memberIds
    """)
    List<MemberIdNameDto> findIdAndNameByIdIn(@Param("memberIds") List<Long> memberIds);
    
    @Query("""
    SELECT m
    FROM Member m
    WHERE m.latitude IS NOT NULL 
    AND m.longitude IS NOT NULL
    AND m.isOnboarded = true
    """)
    List<Member> findAllWithLocation();
}
