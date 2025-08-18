package kr.co.readingtown.bookhouse.repository;

import jakarta.persistence.LockModeType;
import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExchangeStatusRepository extends JpaRepository<ExchangeStatus, Long> {

    List<ExchangeStatus> findByChatroomId(Long chatroomId);

    // 같은 채팅방 내 ACCEPTED 개수 확인(동시성 대비를 위해 같은 채팅방의 행들을 잠그는 쿼리)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select e from ExchangeStatus e
        where e.chatroomId = :chatroomId and e.requestStatus = :requestStatus
    """)
    List<ExchangeStatus> findAllAcceptedByChatRoomId(@Param("chatroomId") Long chatroomId, @Param("requestStatus") RequestStatus requestStatus);
}
