package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginTypeAndLoginId(LoginType loginType, String loginId);

    Member findByLoginTypeAndLoginId(LoginType loginType, String loginId);

    boolean existsByNickname(String candidate);

    //유저 검색
    Page<Member> findByNicknameContainingIgnoreCase(String nickname, Pageable pageable);

}
