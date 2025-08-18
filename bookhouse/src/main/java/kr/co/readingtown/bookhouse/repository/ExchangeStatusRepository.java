package kr.co.readingtown.bookhouse.repository;

import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExchangeStatusRepository extends JpaRepository<ExchangeStatus, Long> {

    List<ExchangeStatus> findByChatroomId(Long chatroomId);
}
