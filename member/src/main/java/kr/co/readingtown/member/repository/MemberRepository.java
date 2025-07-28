package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.Member;
import kr.co.readingtown.member.domain.enums.LoginType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByLoginTypeAndLoginId(LoginType loginType, String loginId);
}
