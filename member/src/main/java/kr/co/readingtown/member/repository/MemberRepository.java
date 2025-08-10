package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import kr.co.readingtown.member.dto.query.MemberIdNameDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginTypeAndLoginId(LoginType loginType, String loginId);

    Member findByLoginTypeAndLoginId(LoginType loginType, String loginId);

    boolean existsByUsername(String candidate);

    @Query("""
    SELECT new kr.co.readingtown.member.dto.query.MemberIdNameDto(
        m.memberId,
        m.username
    )
    FROM Member m
    WHERE m.memberId IN :memberIds
    """)
    List<MemberIdNameDto> findIdAndNameByIdIn(@Param("memberIds") List<Long> memberIds);
}
