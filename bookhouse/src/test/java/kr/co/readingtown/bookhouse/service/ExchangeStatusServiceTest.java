package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import kr.co.readingtown.bookhouse.dto.request.ExchangeRequestDto;
import kr.co.readingtown.bookhouse.dto.response.AcceptExchangeResponseDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangeResponseDto;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
import kr.co.readingtown.bookhouse.integration.chat.ChatClient;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import kr.co.readingtown.bookhouse.repository.ExchangeStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeStatusService 테스트")
class ExchangeStatusServiceTest {

    @Mock
    private BookhouseRepository bookhouseRepository;

    @Mock
    private ExchangeStatusRepository exchangeStatusRepository;

    @Mock
    private ChatClient chatClient;

    @Mock
    private BookhouseService bookhouseService;

    @InjectMocks
    private ExchangeStatusService exchangeStatusService;

    private Long memberId;
    private Long chatroomId;
    private Long exchangeStatusId;

    @BeforeEach
    void setUp() {
        memberId = 1L;
        chatroomId = 100L;
        exchangeStatusId = 10L;
    }

    @Test
    @DisplayName("교환 요청 생성 성공")
    void createExchangeStatus_Success() {
        // Given
        Long partnerBookhouseId = 2L;
        ExchangeRequestDto requestDto = new ExchangeRequestDto(chatroomId, partnerBookhouseId);

        ExchangeStatus savedStatus = ExchangeStatus.builder()
                .chatroomId(chatroomId)
                .bookhouseId(partnerBookhouseId)
                .requestStatus(RequestStatus.REQUEST)
                .build();
        setFieldValue(savedStatus, "exchangeStatusId", exchangeStatusId);

        Bookhouse pendingBookhouse = mock(Bookhouse.class);
        when(pendingBookhouse.getIsExchanged()).thenReturn(IsExchanged.PENDING);

        when(exchangeStatusRepository.existsByChatroomIdAndBookhouseId(chatroomId, partnerBookhouseId))
                .thenReturn(false);
        when(bookhouseRepository.findById(partnerBookhouseId))
                .thenReturn(Optional.of(pendingBookhouse));
        when(exchangeStatusRepository.save(any(ExchangeStatus.class)))
                .thenReturn(savedStatus);

        // When
        ExchangeResponseDto result = exchangeStatusService.createExchangeStatus(memberId, requestDto);

        // Then
        assertThat(result.exchangeStatusId()).isEqualTo(exchangeStatusId);
        assertThat(result.status()).isEqualTo(RequestStatus.REQUEST);
        verify(exchangeStatusRepository, times(1)).save(any(ExchangeStatus.class));
    }

    @Test
    @DisplayName("중복 교환 요청시 예외 발생")
    void createExchangeStatus_Duplicate_ThrowsException() {
        // Given
        Long partnerBookhouseId = 2L;
        ExchangeRequestDto requestDto = new ExchangeRequestDto(chatroomId, partnerBookhouseId);

        when(exchangeStatusRepository.existsByChatroomIdAndBookhouseId(chatroomId, partnerBookhouseId))
                .thenReturn(true);

        // Then
        assertThatThrownBy(() -> exchangeStatusService.createExchangeStatus(memberId, requestDto))
                .isInstanceOf(BookhouseException.DuplicateExchangeRequest.class);
    }

    @Test
    @DisplayName("교환 요청 수락 - 소유자가 아니면 수락 불가")
    void acceptExchangeStatus_NonOwnerCannotAccept() {
        // Given
        Long partnerBookhouseId = 2L;
        Long ownerId = 2L;
        Long requesterId = 1L;

        ExchangeStatus mockExchangeStatus = mock(ExchangeStatus.class);
        when(mockExchangeStatus.getBookhouseId()).thenReturn(partnerBookhouseId);
        when(mockExchangeStatus.getExchangeStatusId()).thenReturn(exchangeStatusId);

        Bookhouse mockBookhouse = mock(Bookhouse.class);
        when(mockBookhouse.getMemberId()).thenReturn(ownerId);

        when(exchangeStatusRepository.findChatroomIdById(exchangeStatusId))
                .thenReturn(chatroomId);
        when(exchangeStatusRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(List.of(mockExchangeStatus));
        when(bookhouseRepository.findById(partnerBookhouseId))
                .thenReturn(Optional.of(mockBookhouse));

        // Then
        assertThatThrownBy(() ->
                exchangeStatusService.acceptExchangeStatus(requesterId, exchangeStatusId))
                .isInstanceOf(BookhouseException.ForbiddenAcceptRequest.class);
    }

    @Test
    @DisplayName("교환 요청 수락 성공 - 한 명만 수락한 경우")
    void acceptExchangeStatus_Success() {
        // Given
        Long partnerBookhouseId = 2L;
        Long ownerId = 2L;

        ExchangeStatus mockExchangeStatus = mock(ExchangeStatus.class);
        when(mockExchangeStatus.getBookhouseId()).thenReturn(partnerBookhouseId);
        when(mockExchangeStatus.getRequestStatus())
                .thenReturn(RequestStatus.REQUEST)
                .thenReturn(RequestStatus.ACCEPTED);
        when(mockExchangeStatus.getChatroomId()).thenReturn(chatroomId);
        when(mockExchangeStatus.getExchangeStatusId()).thenReturn(exchangeStatusId);

        Bookhouse mockBookhouse = mock(Bookhouse.class);
        when(mockBookhouse.getMemberId()).thenReturn(ownerId);

        when(exchangeStatusRepository.findChatroomIdById(exchangeStatusId))
                .thenReturn(chatroomId);
        when(exchangeStatusRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(List.of(mockExchangeStatus));
        when(bookhouseRepository.findById(partnerBookhouseId))
                .thenReturn(Optional.of(mockBookhouse));

        // When
        AcceptExchangeResponseDto result = exchangeStatusService.acceptExchangeStatus(ownerId, exchangeStatusId);

        // Then
        assertThat(result.requestStatus()).isEqualTo(RequestStatus.ACCEPTED);
        assertThat(result.isReserved()).isFalse();
        verify(mockExchangeStatus).updateRequestStatus(RequestStatus.ACCEPTED);
    }

    @Test
    @DisplayName("교환 완료 - RESERVED에서 EXCHANGED로 전환")
    void completeExchange_Success() {
        // Given
        Bookhouse mockMyBook = mock(Bookhouse.class);
        Bookhouse mockPartnerBook = mock(Bookhouse.class);

        when(mockMyBook.getIsExchanged()).thenReturn(IsExchanged.RESERVED);
        when(mockPartnerBook.getIsExchanged()).thenReturn(IsExchanged.RESERVED);

        when(bookhouseRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(Arrays.asList(mockMyBook, mockPartnerBook));

        // When
        exchangeStatusService.completeExchange(chatroomId);

        // Then
        verify(mockMyBook).updateIsExchanged(IsExchanged.EXCHANGED);
        verify(mockPartnerBook).updateIsExchanged(IsExchanged.EXCHANGED);
    }

    @Test
    @DisplayName("교환 완료 실패 - 2개가 아닌 경우")
    void completeExchange_InvalidCount_ThrowsException() {
        // Given
        Bookhouse mockBook = mock(Bookhouse.class);

        when(bookhouseRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(List.of(mockBook));

        // Then
        assertThatThrownBy(() -> exchangeStatusService.completeExchange(chatroomId))
                .isInstanceOf(BookhouseException.InvalidExchangeStatusCount.class);
    }

    @Test
    @DisplayName("교환 완료 실패 - RESERVED 상태가 아닌 경우")
    void completeExchange_NotReserved_ThrowsException() {
        // Given
        Bookhouse mockBook1 = mock(Bookhouse.class);
        Bookhouse mockBook2 = mock(Bookhouse.class);
        when(mockBook1.getIsExchanged()).thenReturn(IsExchanged.PENDING);

        when(bookhouseRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(Arrays.asList(mockBook1, mockBook2));

        // Then
        assertThatThrownBy(() -> exchangeStatusService.completeExchange(chatroomId))
                .isInstanceOf(BookhouseException.InvalidExchangeStatusForComplete.class);
    }

    @Test
    @DisplayName("반납 완료 - EXCHANGED에서 PENDING으로 전환")
    void returnExchange_Success() {
        // Given
        Bookhouse mockMyBook = mock(Bookhouse.class);
        Bookhouse mockPartnerBook = mock(Bookhouse.class);

        when(mockMyBook.getIsExchanged()).thenReturn(IsExchanged.EXCHANGED);
        when(mockPartnerBook.getIsExchanged()).thenReturn(IsExchanged.EXCHANGED);

        when(bookhouseRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(Arrays.asList(mockMyBook, mockPartnerBook));

        // When
        exchangeStatusService.returnExchange(chatroomId);

        // Then
        verify(mockMyBook).updateIsExchanged(IsExchanged.PENDING);
        verify(mockPartnerBook).updateIsExchanged(IsExchanged.PENDING);
        verify(mockMyBook).updateChatroomId(null);
        verify(mockPartnerBook).updateChatroomId(null);
    }

    @Test
    @DisplayName("반납 실패 - EXCHANGED 상태가 아닌 경우")
    void returnExchange_NotExchanged_ThrowsException() {
        // Given
        Bookhouse mockBook1 = mock(Bookhouse.class);
        Bookhouse mockBook2 = mock(Bookhouse.class);
        when(mockBook1.getIsExchanged()).thenReturn(IsExchanged.RESERVED);

        when(bookhouseRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(Arrays.asList(mockBook1, mockBook2));

        // Then
        assertThatThrownBy(() -> exchangeStatusService.returnExchange(chatroomId))
                .isInstanceOf(BookhouseException.InvalidExchangeStatusForReturn.class);
    }

    // === 헬퍼 메소드 ===

    private ExchangeStatus createExchangeStatus(Long id, Long chatroomId, Long bookhouseId, RequestStatus status) {
        ExchangeStatus es = ExchangeStatus.builder()
                .chatroomId(chatroomId)
                .bookhouseId(bookhouseId)
                .requestStatus(status)
                .build();
        setFieldValue(es, "exchangeStatusId", id);
        return es;
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
