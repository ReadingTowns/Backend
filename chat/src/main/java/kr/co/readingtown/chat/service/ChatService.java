package kr.co.readingtown.chat.service;

import kr.co.readingtown.chat.domain.Chatroom;
import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    public final ChatroomRepository chatroomRepository;

    // 기존 채팅룸이 존재하면 반환, 없으면 생성
    // 요청한 책에 대해 교환 상태 하나 생성
    public Long createChatroom(ChatRequestDto chatRequestDto, Long memberId) {

        // 채팅방 조회
        return chatroomRepository.findChatroomIdByMemberIds(chatRequestDto.memberId(), memberId)
                .orElseGet(() -> {
                    // 새 채팅방 생성
                    Chatroom newChatroom = Chatroom.builder()
                            .ownerId(chatRequestDto.memberId())
                            .requesterId(memberId)
                            .build();

                    // 교환 상태 생성


                    return chatroomRepository.save(newChatroom).getChatroomId();
                });
    }
}
