package kr.co.readingtown.chat.service;

import kr.co.readingtown.chat.domain.Chatroom;
import kr.co.readingtown.chat.domain.Message;
import kr.co.readingtown.chat.dto.request.ChatRequestDto;
import kr.co.readingtown.chat.dto.response.*;
import kr.co.readingtown.chat.exception.ChatException;
import kr.co.readingtown.chat.integration.book.BookClient;
import kr.co.readingtown.chat.integration.bookhouse.BookhouseClient;
import kr.co.readingtown.chat.integration.bookhouse.BookhouseUpdater;
import kr.co.readingtown.chat.integration.member.MemberClient;
import kr.co.readingtown.chat.repository.ChatroomRepository;
import kr.co.readingtown.chat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    public final BookClient bookClient;
    public final MemberClient memberClient;
    public final BookhouseClient bookhouseClient;
    public final BookhouseUpdater bookhouseUpdater;

    public final MessageRepository messageRepository;
    public final ChatroomRepository chatroomRepository;


    // 교환하고자 하는 책의 서재에서 채팅 요청
    @Transactional
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

        Long chatroomId = chatroomRepository.save(newChatroom).getChatroomId();
        return new ChatroomIdResponse(chatroomId);
    }

    // 채팅룸 교환 책 정보 조회
    public ChatExchangedBookInfoResponse getExchangedBookInfo(Long chatroomId) {

        ExchangedBookResponse exchangedBooks = bookhouseClient.getBookIdByChatroomId(chatroomId);

        BookInfoResponse myBookInfo = null;
        BookInfoResponse partnerBookInfo = null;

        if (exchangedBooks.myBook() != null)
            myBookInfo = bookClient.getBookInfo(exchangedBooks.myBook().bookId());
        if (exchangedBooks.partnerBook() != null)
            partnerBookInfo = bookClient.getBookInfo(exchangedBooks.partnerBook().bookId());

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

    // 채팅룸 메시지 조회
    public MessageListResponseDto getChatMessage(Long chatroomId, int limit, Long before, Long myId) {

        // 메시지 조회
        List<Message> messages = messageRepository.findMessages(chatroomId, before, PageRequest.of(0, limit));
        Collections.reverse(messages);
        List<MessageResponseDto> messageDtos = messages.stream()
                .map(MessageResponseDto::of)
                .toList();

        // 페이징 정보
        Long nextCursor = messages.isEmpty() ? null : messages.get(0).getMessageId();
        boolean hasMore = messages.size() == limit;
        CursorPagingResponseDto paging = new CursorPagingResponseDto(nextCursor, hasMore);

        return new MessageListResponseDto(myId, messageDtos, paging);
    }

    // 채팅룸 리스트 조회
    public List<ChatroomPreviewResponseDto> getMyChatroom(Long myId) {

        // 내가 속한 채팅룸 전부 조회
        List<Chatroom> chatrooms = chatroomRepository.findMyChatrooms(myId);
        if (chatrooms.isEmpty())
            return List.of();

        // 최신 메시지 맵으로 조회
        Map<Long, Message> latestMessageMap = getLatestMessages(chatrooms);

        return chatrooms.stream()
                .map(chatroom -> toPreviewDto(chatroom, myId, latestMessageMap))
                .toList();
    }

    private Map<Long, Message> getLatestMessages(List<Chatroom> chatrooms) {
        List<Long> chatroomIds = chatrooms.stream()
                .map(Chatroom::getChatroomId)
                .toList();

        List<Message> latestMessages = messageRepository.findLatestMessagesByChatrooms(chatroomIds);

        return latestMessages.stream()
                .collect(Collectors.toMap(Message::getChatroomId, m -> m));
    }

    private ChatroomPreviewResponseDto toPreviewDto(Chatroom chatroom, Long myId, Map<Long, Message> latestMessageMap) {
        Long partnerId = chatroom.getRequesterId().equals(myId)
                ? chatroom.getOwnerId()
                : chatroom.getRequesterId();

        ChatMemberInfoResponse partnerInfo = memberClient.getMemberInfo(partnerId);
        Message message = latestMessageMap.get(chatroom.getChatroomId());

        return ChatroomPreviewResponseDto.of(
                chatroom.getChatroomId(),
                partnerInfo.nickname(),
                message);
    }

    // 채팅룸 나가기 (만약 모두가 나갔다면, 채팅룸 삭제)
    @Transactional
    public void leaveChatroom(Long chatroomId, Long myId) {

        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(ChatException.ChatroomNotFound::new);
        if (!chatroom.hasMember(myId))
            throw new ChatException.MemberNotInChatroom();

        if (Objects.equals(chatroom.getOwnerId(), myId)) {
            chatroom.removeOwnerId();
        } else if (Objects.equals(chatroom.getRequesterId(), myId)) {
            chatroom.removeRequesterId();
        }

        if (chatroom.isEmpty())
            chatroomRepository.delete(chatroom);
    }

    // 대면 교환 완료
    @Transactional
    public void completeExchange(Long chatroomId, Long myId) {
        // 채팅방 존재 및 권한 확인
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(ChatException.ChatroomNotFound::new);
        
        if (!chatroom.hasMember(myId)) {
            throw new ChatException.MemberNotInChatroom();
        }
        
        // Bookhouse 서비스에 교환 완료 요청 (두 Bookhouse 모두 EXCHANGED으로 변경)
        bookhouseUpdater.completeExchange(chatroomId);
    }

    // 대면 반납 완료
    @Transactional
    public void returnExchange(Long chatroomId, Long myId) {
        // 채팅방 존재 및 권한 확인
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(ChatException.ChatroomNotFound::new);
        
        if (!chatroom.hasMember(myId)) {
            throw new ChatException.MemberNotInChatroom();
        }
        
        // Bookhouse 서비스에 교환 반납 요청 (두 Bookhouse 모두 PENDING으로 변경)
        bookhouseUpdater.returnExchange(chatroomId);
    }

    // 채팅 메시지 저장
    @Transactional
    public void saveChatMessage(Long senderId, String roomId, String content) {

        Message message = Message.builder()
                .chatroomId(Long.parseLong(roomId))
                .senderId(senderId)
                .content(content)
                .build();
        messageRepository.save(message);
    }
}
