package kr.co.readingtown.member.repository;

import kr.co.readingtown.member.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
}
