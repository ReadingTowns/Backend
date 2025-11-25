package kr.co.readingtown.chat.repository;

import kr.co.readingtown.chat.domain.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
    SELECT m
    FROM Message m
    WHERE m.chatroomId = :chatroomId
        AND (:before IS NULL OR m.messageId < :before)
    ORDER BY m.messageId DESC
    """)
    List<Message> findMessages(@Param("chatroomId") Long chatroomId,
                               @Param("before") Long before,
                               Pageable pageable);


//    @Query("""
//    SELECT m
//    FROM Message m
//    WHERE m.chatroomId IN :chatroomIds
//    AND m.createdAt = (
//        SELECT MAX (m2.createdAt)
//        FROM Message m2
//        WHERE m2.chatroomId = m.chatroomId
//    )
//    """)
//    List<Message> findLatestMessagesByChatrooms(@Param("chatroomIds") List<Long> chatroomIds);

    @Query(value = """
        SELECT m.*
        FROM messages m
        JOIN (
            SELECT m2.chatroom_id, MAX(m2.created_at) AS latest_created_at
            FROM messages m2
            WHERE m2.chatroom_id IN :chatroomIds
            GROUP BY m2.chatroom_id
        ) latest ON latest.chatroom_id = m.chatroom_id
        WHERE m.created_at = latest.latest_created_at
    """, nativeQuery = true)
    List<Message> findLatestMessagesByChatrooms(@Param("chatroomIds") List<Long> chatroomIds);

    // 채팅방의 모든 메시지 삭제
    void deleteByChatroomId(Long chatroomId);

    @Modifying
    @Query("""
    DELETE
    FROM Message m
    WHERE m.chatroomId IN (:chatroomIds)
    """)
    void deleteMessageByChatroomId(@Param("chatroomIds") List<Long> chatroomIds);
}
