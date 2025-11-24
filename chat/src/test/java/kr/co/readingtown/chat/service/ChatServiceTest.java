package kr.co.readingtown.chat.service;

import kr.co.readingtown.chat.domain.Chatroom;
import kr.co.readingtown.chat.dto.response.ExchangeStatusResponse;
import kr.co.readingtown.chat.exception.ChatException;
import kr.co.readingtown.chat.integration.bookhouse.BookhouseClient;
import kr.co.readingtown.chat.repository.ChatroomRepository;
import kr.co.readingtown.chat.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ChatService 채팅방 나가기 테스트")
class ChatServiceTest {

    @Mock
    private ChatroomRepository chatroomRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private BookhouseClient bookhouseClient;

    @InjectMocks
    private ChatService chatService;

    private Long chatroomId;
    private Long myId;
    private Long partnerId;
    private Chatroom chatroom;

    @BeforeEach
    void setUp() {
        chatroomId = 100L;
        myId = 1L;
        partnerId = 2L;
        chatroom = Chatroom.builder()
                .ownerId(myId)
                .requesterId(partnerId)
                .build();
    }

    @Nested
    @DisplayName("채팅방 나가기 성공 케이스")
    class SuccessCases {

        @Test
        @DisplayName("교환 중이 아닌 채팅방 나가기 성공 - ExchangeStatus가 null")
        void leaveChatroom_NoExchangeStatus_Success() {
            // Given
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));
            when(bookhouseClient.getExchangeStatus(chatroomId)).thenReturn(null);

            // When
            chatService.leaveChatroom(chatroomId, myId);

            // Then
            verify(messageRepository).deleteByChatroomId(chatroomId);
            verify(chatroomRepository).delete(chatroom);
        }

        @Test
        @DisplayName("교환 중이 아닌 채팅방 나가기 성공 - ExchangeStatus.status가 null")
        void leaveChatroom_ExchangeStatusNull_Success() {
            // Given
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));
            when(bookhouseClient.getExchangeStatus(chatroomId))
                    .thenReturn(new ExchangeStatusResponse(null));

            // When
            chatService.leaveChatroom(chatroomId, myId);

            // Then
            verify(messageRepository).deleteByChatroomId(chatroomId);
            verify(chatroomRepository).delete(chatroom);
        }

        @Test
        @DisplayName("교환 중이 아닌 채팅방 나가기 성공 - PENDING 상태")
        void leaveChatroom_PendingStatus_Success() {
            // Given
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));
            when(bookhouseClient.getExchangeStatus(chatroomId))
                    .thenReturn(new ExchangeStatusResponse("PENDING"));

            // When
            chatService.leaveChatroom(chatroomId, myId);

            // Then
            verify(messageRepository).deleteByChatroomId(chatroomId);
            verify(chatroomRepository).delete(chatroom);
        }

        @Test
        @DisplayName("핵심 케이스: 반납 완료 후 다른 채팅방에서 교환 중일 때 나가기 가능")
        void leaveChatroom_BookExchangedInDifferentChatroom_Success() {
            // Given
            // 채팅방 1에서 반납 완료 (Bookhouse.chatroomId = null)
            // 채팅방 2에서 같은 책 교환 중 (Bookhouse.chatroomId = chatroomId2)
            // getExchangeStatus(chatroomId1) → null 반환 (해당 채팅방과 연결된 Bookhouse 없음)
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));
            when(bookhouseClient.getExchangeStatus(chatroomId)).thenReturn(null);

            // When
            chatService.leaveChatroom(chatroomId, myId);

            // Then
            verify(messageRepository).deleteByChatroomId(chatroomId);
            verify(chatroomRepository).delete(chatroom);
        }
    }

    @Nested
    @DisplayName("채팅방 나가기 실패 케이스")
    class FailureCases {

        @Test
        @DisplayName("예외 케이스 1: 채팅방이 존재하지 않음")
        void leaveChatroom_ChatroomNotFound_ThrowsException() {
            // Given
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> chatService.leaveChatroom(chatroomId, myId))
                    .isInstanceOf(ChatException.ChatroomNotFound.class);

            verify(messageRepository, never()).deleteByChatroomId(any());
            verify(chatroomRepository, never()).delete(any());
        }

        @Test
        @DisplayName("예외 케이스 2: 사용자가 채팅방 멤버가 아님")
        void leaveChatroom_MemberNotInChatroom_ThrowsException() {
            // Given
            Long notMemberId = 999L;
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));

            // When & Then
            assertThatThrownBy(() -> chatService.leaveChatroom(chatroomId, notMemberId))
                    .isInstanceOf(ChatException.MemberNotInChatroom.class);

            verify(bookhouseClient, never()).getExchangeStatus(any());
            verify(messageRepository, never()).deleteByChatroomId(any());
            verify(chatroomRepository, never()).delete(any());
        }

        @Test
        @DisplayName("예외 케이스 3: RESERVED 상태일 때 나가기 불가")
        void leaveChatroom_ReservedStatus_ThrowsException() {
            // Given
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));
            when(bookhouseClient.getExchangeStatus(chatroomId))
                    .thenReturn(new ExchangeStatusResponse("RESERVED"));

            // When & Then
            assertThatThrownBy(() -> chatService.leaveChatroom(chatroomId, myId))
                    .isInstanceOf(ChatException.CannotLeaveDuringReservation.class);

            verify(messageRepository, never()).deleteByChatroomId(any());
            verify(chatroomRepository, never()).delete(any());
        }

        @Test
        @DisplayName("예외 케이스 4: EXCHANGED 상태일 때 나가기 불가")
        void leaveChatroom_ExchangedStatus_ThrowsException() {
            // Given
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));
            when(bookhouseClient.getExchangeStatus(chatroomId))
                    .thenReturn(new ExchangeStatusResponse("EXCHANGED"));

            // When & Then
            assertThatThrownBy(() -> chatService.leaveChatroom(chatroomId, myId))
                    .isInstanceOf(ChatException.CannotLeaveDuringExchange.class);

            verify(messageRepository, never()).deleteByChatroomId(any());
            verify(chatroomRepository, never()).delete(any());
        }
    }

    @Nested
    @DisplayName("메시지와 채팅방 삭제 순서 검증")
    class DeletionOrderTests {

        @Test
        @DisplayName("메시지를 먼저 삭제한 후 채팅방을 삭제한다")
        void leaveChatroom_DeletesMessagesBeforeChatroom() {
            // Given
            when(chatroomRepository.findById(chatroomId)).thenReturn(Optional.of(chatroom));
            when(bookhouseClient.getExchangeStatus(chatroomId)).thenReturn(null);

            // When
            chatService.leaveChatroom(chatroomId, myId);

            // Then
            var inOrder = inOrder(messageRepository, chatroomRepository);
            inOrder.verify(messageRepository).deleteByChatroomId(chatroomId);
            inOrder.verify(chatroomRepository).delete(chatroom);
        }
    }
}
