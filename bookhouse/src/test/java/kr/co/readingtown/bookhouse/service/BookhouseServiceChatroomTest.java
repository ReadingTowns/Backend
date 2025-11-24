package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.dto.response.ExchangeStatusResponse;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookhouseService - 채팅방 교환 상태 조회 테스트")
class BookhouseServiceChatroomTest {

    @Mock
    private BookhouseRepository bookhouseRepository;

    @InjectMocks
    private BookhouseService bookhouseService;

    @Test
    @DisplayName("시나리오 1: 채팅방에 책이 없을 때 null 반환 (반납 완료)")
    void getIsExchangedForChatroom_NoBooksInChatroom_ReturnsNull() {
        // Given
        Long chatroomId1 = 100L;
        when(bookhouseRepository.findAllByChatroomId(chatroomId1))
                .thenReturn(List.of());

        // When
        ExchangeStatusResponse result = bookhouseService.getIsExchangedForChatroom(chatroomId1);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("시나리오 2: 같은 책이 다른 채팅방에서 교환 중일 때 null 반환")
    void getIsExchangedForChatroom_BookExchangedInDifferentChatroom_ReturnsNull() {
        // Given
        // 채팅방 1: 반납 완료 (chatroomId = null)
        // 채팅방 2: A책이 교환 중 (chatroomId = 200L)
        Long chatroomId1 = 100L;

        // 채팅방 1 조회 시 빈 리스트 (A책의 chatroomId가 200이므로)
        when(bookhouseRepository.findAllByChatroomId(chatroomId1))
                .thenReturn(List.of());

        // When
        ExchangeStatusResponse result = bookhouseService.getIsExchangedForChatroom(chatroomId1);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("시나리오 3: 채팅방에 PENDING 상태 책이 있을 때 PENDING 반환")
    void getIsExchangedForChatroom_PendingStatus_ReturnsPending() {
        // Given
        Long chatroomId = 100L;
        Bookhouse book1 = createBookhouse(1L, 1L, 10L, IsExchanged.PENDING, chatroomId);
        Bookhouse book2 = createBookhouse(2L, 2L, 20L, IsExchanged.PENDING, chatroomId);

        when(bookhouseRepository.findAllByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(book1, book2));

        // When
        ExchangeStatusResponse result = bookhouseService.getIsExchangedForChatroom(chatroomId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("시나리오 4: 채팅방에 RESERVED 상태 책이 있을 때 RESERVED 반환")
    void getIsExchangedForChatroom_ReservedStatus_ReturnsReserved() {
        // Given
        Long chatroomId = 200L;
        Bookhouse book1 = createBookhouse(3L, 1L, 30L, IsExchanged.RESERVED, chatroomId);
        Bookhouse book2 = createBookhouse(4L, 2L, 40L, IsExchanged.RESERVED, chatroomId);

        when(bookhouseRepository.findAllByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(book1, book2));

        // When
        ExchangeStatusResponse result = bookhouseService.getIsExchangedForChatroom(chatroomId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("RESERVED");
    }

    @Test
    @DisplayName("시나리오 5: 채팅방에 EXCHANGED 상태 책이 있을 때 EXCHANGED 반환")
    void getIsExchangedForChatroom_ExchangedStatus_ReturnsExchanged() {
        // Given
        Long chatroomId = 300L;
        Bookhouse book1 = createBookhouse(5L, 1L, 50L, IsExchanged.EXCHANGED, chatroomId);
        Bookhouse book2 = createBookhouse(6L, 2L, 60L, IsExchanged.EXCHANGED, chatroomId);

        when(bookhouseRepository.findAllByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(book1, book2));

        // When
        ExchangeStatusResponse result = bookhouseService.getIsExchangedForChatroom(chatroomId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.status()).isEqualTo("EXCHANGED");
    }

    @Test
    @DisplayName("핵심 시나리오: 반납 완료 후 다른 채팅방에서 교환 중 - 통합 시나리오")
    void getIsExchangedForChatroom_CompleteScenario_WorksCorrectly() {
        // Given
        Long chatroomId1 = 100L; // 반납 완료된 채팅방
        Long chatroomId2 = 200L; // 현재 교환 중인 채팅방

        // A책: chatroomId2에서 RESERVED 상태
        Bookhouse aBook = createBookhouse(1L, 1L, 10L, IsExchanged.RESERVED, chatroomId2);
        Bookhouse cBook = createBookhouse(2L, 2L, 20L, IsExchanged.RESERVED, chatroomId2);

        // 채팅방 1 조회: 빈 리스트 (A책의 chatroomId는 200)
        when(bookhouseRepository.findAllByChatroomId(chatroomId1))
                .thenReturn(List.of());

        // 채팅방 2 조회: A책, C책 반환
        when(bookhouseRepository.findAllByChatroomId(chatroomId2))
                .thenReturn(Arrays.asList(aBook, cBook));

        // When & Then
        // 채팅방 1 나가기 가능 (null 반환)
        ExchangeStatusResponse result1 = bookhouseService.getIsExchangedForChatroom(chatroomId1);
        assertThat(result1).isNull();

        // 채팅방 2 나가기 불가 (RESERVED 반환)
        ExchangeStatusResponse result2 = bookhouseService.getIsExchangedForChatroom(chatroomId2);
        assertThat(result2).isNotNull();
        assertThat(result2.status()).isEqualTo("RESERVED");
    }

    // === 헬퍼 메소드 ===

    private Bookhouse createBookhouse(Long bookhouseId, Long memberId, Long bookId,
                                      IsExchanged status, Long chatroomId) {
        Bookhouse bookhouse = Bookhouse.builder()
                .memberId(memberId)
                .bookId(bookId)
                .isExchanged(status)
                .chatroomId(chatroomId)
                .build();
        // 테스트를 위해 ID 설정 (실제로는 DB가 자동 생성)
        setFieldValue(bookhouse, "bookhouseId", bookhouseId);
        return bookhouse;
    }

    private void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(obj, value);
        } catch (Exception e) {
            throw new RuntimeException("테스트 중 리플렉션 오류", e);
        }
    }
}
