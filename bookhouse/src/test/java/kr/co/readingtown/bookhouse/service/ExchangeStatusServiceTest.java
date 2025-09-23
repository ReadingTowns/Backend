package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.ExchangeStatus;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.domain.enums.RequestStatus;
import kr.co.readingtown.bookhouse.dto.request.ExchangeRequestDto;
import kr.co.readingtown.bookhouse.dto.response.AcceptExchangeResponseDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangeResponseDto;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeStatusService 테스트")
class ExchangeStatusServiceTest {

    @Mock
    private BookhouseRepository bookhouseRepository;

    @Mock
    private ExchangeStatusRepository exchangeStatusRepository;
    
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
        
        // 저장될 ExchangeStatus
        ExchangeStatus savedStatus = ExchangeStatus.builder()
                .chatroomId(chatroomId)
                .bookhouseId(partnerBookhouseId)
                .requestStatus(RequestStatus.REQUEST)
                .build();
        setFieldValue(savedStatus, "exchangeStatusId", exchangeStatusId);
        
        // When - 필요한 stubbing만 설정
        when(exchangeStatusRepository.existsByChatroomIdAndBookhouseId(chatroomId, partnerBookhouseId))
                .thenReturn(false);
        when(exchangeStatusRepository.save(any(ExchangeStatus.class)))
                .thenReturn(savedStatus);
        
        // Then
        ExchangeResponseDto result = exchangeStatusService.createExchangeStatus(memberId, requestDto);
        
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
        
        // When
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
        Long ownerId = 2L;  // 책 소유자
        Long requesterId = 1L;  // 요청자 (소유자가 아님)
        
        // Mock 객체 생성
        ExchangeStatus mockExchangeStatus = mock(ExchangeStatus.class);
        when(mockExchangeStatus.getBookhouseId()).thenReturn(partnerBookhouseId);
        // getRequestStatus()는 제거 - 예외 발생 전에 사용되지 않음
        
        Bookhouse mockBookhouse = mock(Bookhouse.class);
        when(mockBookhouse.getMemberId()).thenReturn(ownerId);
        
        // When
        when(exchangeStatusRepository.findById(exchangeStatusId))
                .thenReturn(Optional.of(mockExchangeStatus));
        when(bookhouseRepository.findById(partnerBookhouseId))
                .thenReturn(Optional.of(mockBookhouse));
        
        // Then - 소유자가 아닌 사람이 수락하려 하면 예외
        assertThatThrownBy(() -> 
                exchangeStatusService.acceptExchangeStatus(requesterId, exchangeStatusId))
                .isInstanceOf(BookhouseException.ForbiddenAcceptRequest.class);
    }
    
    @Test
    @DisplayName("교환 요청 수락 성공 - 소유자가 수락")
    void acceptExchangeStatus_Success() {
        // Given
        Long partnerBookhouseId = 2L;
        Long ownerId = 2L;  // 책 소유자
        
        // Mock 객체 생성
        ExchangeStatus mockExchangeStatus = mock(ExchangeStatus.class);
        when(mockExchangeStatus.getBookhouseId()).thenReturn(partnerBookhouseId);
        when(mockExchangeStatus.getRequestStatus())
                .thenReturn(RequestStatus.REQUEST)  // 처음 체크할 때
                .thenReturn(RequestStatus.ACCEPTED); // 업데이트 후 반환할 때
        when(mockExchangeStatus.getChatroomId()).thenReturn(chatroomId);
        
        Bookhouse mockBookhouse = mock(Bookhouse.class);
        when(mockBookhouse.getMemberId()).thenReturn(ownerId);
        
        // When
        when(exchangeStatusRepository.findById(exchangeStatusId))
                .thenReturn(Optional.of(mockExchangeStatus));
        when(bookhouseRepository.findById(partnerBookhouseId))
                .thenReturn(Optional.of(mockBookhouse));
        when(exchangeStatusRepository.findAllByChatroomIdForUpdate(chatroomId))
                .thenReturn(List.of(mockExchangeStatus));  // 1개만 수락됨
        
        // Then - 소유자가 수락하면 성공
        AcceptExchangeResponseDto result = exchangeStatusService.acceptExchangeStatus(ownerId, exchangeStatusId);
        
        assertThat(result.requestStatus()).isEqualTo(RequestStatus.ACCEPTED);
        assertThat(result.isReserved()).isFalse();  // 1개만 수락이므로 false
        verify(mockExchangeStatus).updateRequestStatus(RequestStatus.ACCEPTED);
    }
    
    @Test
    @DisplayName("교환 완료 - RESERVED에서 EXCHANGED로 전환")
    void completeExchange_Success() {
        // Given - Mock 객체 사용
        Bookhouse mockMyBook = mock(Bookhouse.class);
        Bookhouse mockPartnerBook = mock(Bookhouse.class);
        
        when(mockMyBook.getIsExchanged()).thenReturn(IsExchanged.RESERVED);
        when(mockPartnerBook.getIsExchanged()).thenReturn(IsExchanged.RESERVED);
        
        ExchangeStatus status1 = createExchangeStatus(1L, chatroomId, 1L, RequestStatus.ACCEPTED);
        ExchangeStatus status2 = createExchangeStatus(2L, chatroomId, 2L, RequestStatus.ACCEPTED);
        
        // When
        when(exchangeStatusRepository.findByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(status1, status2));
        when(bookhouseRepository.findById(1L))
                .thenReturn(Optional.of(mockMyBook));
        when(bookhouseRepository.findById(2L))
                .thenReturn(Optional.of(mockPartnerBook));
        
        // Then
        exchangeStatusService.completeExchange(chatroomId);
        
        // Mock 객체에 대해서만 verify 사용
        verify(mockMyBook).updateIsExchanged(IsExchanged.EXCHANGED);
        verify(mockPartnerBook).updateIsExchanged(IsExchanged.EXCHANGED);
    }
    
    @Test
    @DisplayName("교환 완료 실패 - 2개가 아닌 경우")
    void completeExchange_InvalidCount_ThrowsException() {
        // Given
        ExchangeStatus status1 = createExchangeStatus(1L, chatroomId, 1L, RequestStatus.ACCEPTED);
        
        // When - 1개만 있는 경우
        when(exchangeStatusRepository.findByChatroomId(chatroomId))
                .thenReturn(List.of(status1));
        
        // Then
        assertThatThrownBy(() -> exchangeStatusService.completeExchange(chatroomId))
                .isInstanceOf(BookhouseException.InvalidExchangeStatusCount.class);
    }
    
    @Test
    @DisplayName("교환 완료 실패 - RESERVED 상태가 아닌 경우")
    void completeExchange_NotReserved_ThrowsException() {
        // Given
        Bookhouse mockBook = mock(Bookhouse.class);
        when(mockBook.getIsExchanged()).thenReturn(IsExchanged.PENDING);  // RESERVED가 아님
        
        ExchangeStatus status1 = createExchangeStatus(1L, chatroomId, 1L, RequestStatus.ACCEPTED);
        ExchangeStatus status2 = createExchangeStatus(2L, chatroomId, 2L, RequestStatus.ACCEPTED);
        
        // When
        when(exchangeStatusRepository.findByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(status1, status2));
        when(bookhouseRepository.findById(1L))
                .thenReturn(Optional.of(mockBook));
        
        // Then
        assertThatThrownBy(() -> exchangeStatusService.completeExchange(chatroomId))
                .isInstanceOf(BookhouseException.InvalidExchangeStatusForComplete.class);
    }
    
    @Test
    @DisplayName("반납 완료 - EXCHANGED에서 PENDING으로 전환")
    void returnExchange_Success() {
        // Given - Mock 객체 사용
        Bookhouse mockMyBook = mock(Bookhouse.class);
        Bookhouse mockPartnerBook = mock(Bookhouse.class);
        
        when(mockMyBook.getIsExchanged()).thenReturn(IsExchanged.EXCHANGED);
        when(mockPartnerBook.getIsExchanged()).thenReturn(IsExchanged.EXCHANGED);
        
        ExchangeStatus status1 = createExchangeStatus(1L, chatroomId, 1L, RequestStatus.ACCEPTED);
        ExchangeStatus status2 = createExchangeStatus(2L, chatroomId, 2L, RequestStatus.ACCEPTED);
        
        // When  
        when(exchangeStatusRepository.findByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(status1, status2));
        when(bookhouseRepository.findById(1L))
                .thenReturn(Optional.of(mockMyBook));
        when(bookhouseRepository.findById(2L))
                .thenReturn(Optional.of(mockPartnerBook));
        
        // Then
        exchangeStatusService.returnExchange(chatroomId);
        
        // Mock 객체에 대해서만 verify 사용
        verify(mockMyBook).updateIsExchanged(IsExchanged.PENDING);
        verify(mockPartnerBook).updateIsExchanged(IsExchanged.PENDING);
        verify(mockMyBook).updateChatroomId(null);
        verify(mockPartnerBook).updateChatroomId(null);
    }
    
    @Test  
    @DisplayName("반납 실패 - EXCHANGED 상태가 아닌 경우")
    void returnExchange_NotExchanged_ThrowsException() {
        // Given
        Bookhouse mockBook = mock(Bookhouse.class);
        when(mockBook.getIsExchanged()).thenReturn(IsExchanged.RESERVED);  // EXCHANGED가 아님
        
        ExchangeStatus status1 = createExchangeStatus(1L, chatroomId, 1L, RequestStatus.ACCEPTED);
        ExchangeStatus status2 = createExchangeStatus(2L, chatroomId, 2L, RequestStatus.ACCEPTED);
        
        // When
        when(exchangeStatusRepository.findByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(status1, status2));
        when(bookhouseRepository.findById(1L))
                .thenReturn(Optional.of(mockBook));
        
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