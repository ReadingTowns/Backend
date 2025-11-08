package kr.co.readingtown.chat.repository;

import kr.co.readingtown.chat.domain.ChatBotMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatBotMessageRepository extends JpaRepository<ChatBotMessage, Long> {
    
    List<ChatBotMessage> findByMemberIdOrderByCreatedAtDesc(Long memberId);

    List<ChatBotMessage> findTop20ByMemberIdOrderByCreatedAtDesc(Long memberId);

    // Pagination용 메서드
    List<ChatBotMessage> findByMemberIdOrderByCreatedAtDesc(Long memberId, org.springframework.data.domain.Pageable pageable);

    List<ChatBotMessage> findByMemberIdAndMessageIdLessThanOrderByCreatedAtDesc(Long memberId, Long messageId, org.springframework.data.domain.Pageable pageable);
}