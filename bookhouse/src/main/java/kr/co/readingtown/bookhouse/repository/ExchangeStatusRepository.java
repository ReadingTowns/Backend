package kr.co.readingtown.bookhouse.repository;

import jakarta.persistence.LockModeType;
import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExchangeStatusRepository extends JpaRepository<ExchangeStatus, Long> {

    List<ExchangeStatus> findByChatroomId(Long chatroomId);

    boolean existsByChatroomIdAndBookhouseId(Long chatRoomId, Long bookhouseId);

    //같은 채팅방
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select e from ExchangeStatus e
    where e.chatroomId = :chatroomId
    """)
    List<ExchangeStatus> findAllByChatroomIdForUpdate(@Param("chatroomId") Long chatroomId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from ExchangeStatus e where e.exchangeStatusId = :exchangeStatusId")
    Optional<ExchangeStatus> findByIdForUpdate(@Param("exchangeStatusId") Long id);

    @Modifying
    @Query("""
    DELETE
    FROM ExchangeStatus es
    WHERE es.chatroomId IN (:chatroomIds)
    """)
    void deleteExchangeStatusByChatroomId(@Param("chatroomIds") List<Long> chatroomIds);
}
