package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginTypeAndLoginId(LoginType loginType, String loginId);

    Member findByLoginTypeAndLoginId(LoginType loginType, String loginId);

    boolean existsByNickname(String candidate);

    //유저 검색
    List<Member> findByNicknameContainingIgnoreCase(String nickname);

}
