package kr.co.readingtown.chat.repository;

import kr.co.readingtown.chat.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {

    @Query("""
    SELECT cr.chatroomId
    FROM Chatroom cr
    WHERE cr.ownerId = :ownerId
        AND cr.requesterId = :requesterId
    """)
    Optional<Long> findChatroomIdByMemberIds(@Param("ownerId") Long ownerId, @Param("requesterId")Long requesterId);

    @Query("""
    SELECT cr
    FROM Chatroom cr
    WHERE cr.requesterId = :memberId
        OR cr.ownerId = :memberId
    """)
    List<Chatroom> findMyChatrooms(@Param("memberId") Long memberId);
}
