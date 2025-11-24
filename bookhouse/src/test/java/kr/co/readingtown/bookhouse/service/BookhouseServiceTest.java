package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.dto.response.ExchangingBookResponse;
import kr.co.readingtown.bookhouse.dto.response.BookhouseBookResponseDto;
import kr.co.readingtown.bookhouse.integration.book.BookReader;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookhouseService 교환 중인 책 조회 테스트 - 실제 도메인 모델 기반")
class BookhouseServiceTest {

    @Mock
    private BookhouseRepository bookhouseRepository;
    
    @Mock
    private BookReader bookReader;
    
    @InjectMocks
    private BookhouseService bookhouseService;
    
    private Long memberId;
    private Long chatroomId;
    
    @BeforeEach
    void setUp() {
        memberId = 1L;
        chatroomId = 100L;
    }
    
    @Test
    @DisplayName("교환 중인 책이 없을 때 빈 리스트 반환")
    void getMyExchangingBooks_NoBooks_ReturnsEmpty() {
        // Given
        when(bookhouseRepository.findByMemberIdAndIsExchanged(memberId, IsExchanged.EXCHANGED))
                .thenReturn(List.of());
        
        // When
        List<ExchangingBookResponse> result = bookhouseService.getMyExchangingBooks(memberId);
        
        // Then
        assertThat(result).isEmpty();
        verify(bookhouseRepository, never()).findAllByChatroomId(any());
    }
    
    @Test
    @DisplayName("교환 중인 책 1개 정상 조회")
    void getMyExchangingBooks_OneExchange_Success() {
        // Given
        // 내 책
        Bookhouse myBook = createBookhouse(1L, memberId, 10L, IsExchanged.EXCHANGED, chatroomId);
        
        // 상대방 책  
        Bookhouse partnerBook = createBookhouse(2L, 2L, 20L, IsExchanged.EXCHANGED, chatroomId);
        
        // 책 정보 DTO (실제 생성자 순서: bookId, bookImage, bookName, author)
        BookhouseBookResponseDto myBookInfo = new BookhouseBookResponseDto(
                10L, "http://image1.jpg", "내 책 제목", "작가1"
        );
        
        BookhouseBookResponseDto partnerBookInfo = new BookhouseBookResponseDto(
                20L, "http://image2.jpg", "상대방 책 제목", "작가2"  
        );
        
        // When
        when(bookhouseRepository.findByMemberIdAndIsExchanged(memberId, IsExchanged.EXCHANGED))
                .thenReturn(List.of(myBook));
        when(bookhouseRepository.findAllByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(myBook, partnerBook));
        when(bookReader.getBookInfo(List.of(10L)))
                .thenReturn(List.of(myBookInfo));
        when(bookReader.getBookInfo(List.of(20L)))
                .thenReturn(List.of(partnerBookInfo));
        
        // Then
        List<ExchangingBookResponse> result = bookhouseService.getMyExchangingBooks(memberId);
        
        assertThat(result).hasSize(1);
        
        ExchangingBookResponse response = result.get(0);
        assertThat(response.chatroomId()).isEqualTo(chatroomId);
        
        // 내 책 정보 확인
        assertThat(response.myBook().bookhouseId()).isEqualTo(1L);
        assertThat(response.myBook().bookName()).isEqualTo("내 책 제목");
        assertThat(response.myBook().bookImage()).isEqualTo("http://image1.jpg");
        
        // 상대방 책 정보 확인  
        assertThat(response.partnerBook().bookhouseId()).isEqualTo(2L);
        assertThat(response.partnerBook().bookName()).isEqualTo("상대방 책 제목");
        assertThat(response.partnerBook().bookImage()).isEqualTo("http://image2.jpg");
    }
    
    @Test
    @DisplayName("여러 채팅방에서 교환 중인 책들 조회")
    void getMyExchangingBooks_MultipleExchanges_Success() {
        // Given
        Long chatroomId2 = 200L;
        
        // 첫 번째 채팅방
        Bookhouse myBook1 = createBookhouse(1L, memberId, 10L, IsExchanged.EXCHANGED, chatroomId);
        Bookhouse partnerBook1 = createBookhouse(2L, 2L, 20L, IsExchanged.EXCHANGED, chatroomId);
        
        // 두 번째 채팅방
        Bookhouse myBook2 = createBookhouse(3L, memberId, 30L, IsExchanged.EXCHANGED, chatroomId2);
        Bookhouse partnerBook2 = createBookhouse(4L, 3L, 40L, IsExchanged.EXCHANGED, chatroomId2);
        
        // 책 정보 Mock
        BookhouseBookResponseDto bookInfo1 = new BookhouseBookResponseDto(
                10L, "http://image1.jpg", "책1", "작가1"
        );
        BookhouseBookResponseDto bookInfo2 = new BookhouseBookResponseDto(
                20L, "http://image2.jpg", "책2", "작가2"
        );
        BookhouseBookResponseDto bookInfo3 = new BookhouseBookResponseDto(
                30L, "http://image3.jpg", "책3", "작가3"
        );
        BookhouseBookResponseDto bookInfo4 = new BookhouseBookResponseDto(
                40L, "http://image4.jpg", "책4", "작가4"
        );
        
        // When
        when(bookhouseRepository.findByMemberIdAndIsExchanged(memberId, IsExchanged.EXCHANGED))
                .thenReturn(Arrays.asList(myBook1, myBook2));
        when(bookhouseRepository.findAllByChatroomId(chatroomId))
                .thenReturn(Arrays.asList(myBook1, partnerBook1));
        when(bookhouseRepository.findAllByChatroomId(chatroomId2))
                .thenReturn(Arrays.asList(myBook2, partnerBook2));
        
        when(bookReader.getBookInfo(List.of(10L))).thenReturn(List.of(bookInfo1));
        when(bookReader.getBookInfo(List.of(20L))).thenReturn(List.of(bookInfo2));
        when(bookReader.getBookInfo(List.of(30L))).thenReturn(List.of(bookInfo3));
        when(bookReader.getBookInfo(List.of(40L))).thenReturn(List.of(bookInfo4));
        
        // Then
        List<ExchangingBookResponse> result = bookhouseService.getMyExchangingBooks(memberId);
        
        assertThat(result).hasSize(2);
        
        // 모든 채팅방이 포함되었는지 확인
        assertThat(result.stream().map(ExchangingBookResponse::chatroomId))
                .containsExactlyInAnyOrder(chatroomId, chatroomId2);
    }
    
    @Test
    @DisplayName("상대방 책이 없는 경우 (상대방이 나간 경우) null 처리")
    void getMyExchangingBooks_NoPartnerBook_HandlesNull() {
        // Given
        Bookhouse myBook = createBookhouse(1L, memberId, 10L, IsExchanged.EXCHANGED, chatroomId);
        
        BookhouseBookResponseDto myBookInfo = new BookhouseBookResponseDto(
                10L, "http://image.jpg", "내 책", "작가"
        );
        
        // When
        when(bookhouseRepository.findByMemberIdAndIsExchanged(memberId, IsExchanged.EXCHANGED))
                .thenReturn(List.of(myBook));
        when(bookhouseRepository.findAllByChatroomId(chatroomId))
                .thenReturn(List.of(myBook));  // 내 책만 있음
        when(bookReader.getBookInfo(List.of(10L)))
                .thenReturn(List.of(myBookInfo));
        
        // Then
        List<ExchangingBookResponse> result = bookhouseService.getMyExchangingBooks(memberId);
        
        assertThat(result).hasSize(1);
        
        ExchangingBookResponse response = result.get(0);
        assertThat(response.myBook().bookName()).isEqualTo("내 책");
        
        // 상대방 책 정보가 null
        assertThat(response.partnerBook().bookhouseId()).isNull();
        assertThat(response.partnerBook().bookName()).isNull();
        assertThat(response.partnerBook().bookImage()).isNull();
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