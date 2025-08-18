package kr.co.readingtown.chat.service;

import kr.co.readingtown.chat.domain.Chatroom;
import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.dto.response.*;
import kr.co.readingtown.chat.exception.ChatException;
import kr.co.readingtown.chat.integration.book.BookClient;
import kr.co.readingtown.chat.integration.bookhouse.BookhouseClient;
import kr.co.readingtown.chat.integration.member.MemberClient;
import kr.co.readingtown.chat.repository.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    public final BookClient bookClient;
    public final MemberClient memberClient;
    public final BookhouseClient bookhouseClient;

    public final ChatroomRepository chatroomRepository;


    // 교환하고자 하는 책의 서재에서 채팅 요청
    public ChatroomIdResponse createChatroom(ChatRequestDto chatRequestDto, Long memberId) {

        return chatroomRepository.findChatroomIdByMemberIds(chatRequestDto.memberId(), memberId)
                .map(ChatroomIdResponse::new)
                .orElseGet(() -> createNewChatroom(chatRequestDto, memberId));
    }

    private ChatroomIdResponse createNewChatroom(ChatRequestDto chatRequestDto, Long memberId) {

        Chatroom newChatroom = Chatroom.builder()
                .ownerId(chatRequestDto.memberId())
                .requesterId(memberId)
                .build();

        // 교환 상태 생성 (bookhouse 모듈 호출)
        bookhouseClient.createExchangeStatus(chatRequestDto);

        Long chatroomId = chatroomRepository.save(newChatroom).getChatroomId();
        return new ChatroomIdResponse(chatroomId);
    }

    // 채팅룸 교환 책 정보 조회
    public ChatExchangedBookInfoResponse getExchangedBookInfo(Long chatroomId) {

        ExchangedBookResponse exchangedBooks = bookhouseClient.getBookIdByChatroomId(chatroomId);

        BookInfoResponse myBookInfo = bookClient.getBookInfo(exchangedBooks.myBook().bookId());
        BookInfoResponse partnerBookInfo = bookClient.getBookInfo(exchangedBooks.partnerBook().bookId());

        return ChatExchangedBookInfoResponse.from(exchangedBooks, myBookInfo, partnerBookInfo);
    }

    // 채팅룸 상대방 정보 조회
    public ChatMemberInfoResponse getPartnerInfo(Long chatroomId, Long myId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(ChatException.ChatroomNotFound::new);

        Long partnerId = chatroom.getOwnerId().equals(myId)
                ? chatroom.getRequesterId()
                : chatroom.getOwnerId();

        return memberClient.getMemberInfo(partnerId);
    }
}
