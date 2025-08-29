package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.domain.enums.IsExchanged;
import kr.co.readingtown.bookhouse.dto.request.BookInfoRequestDto;
import kr.co.readingtown.bookhouse.dto.response.BookPreviewResponseDto;
import kr.co.readingtown.bookhouse.dto.response.ExchangingBookDetail;
import kr.co.readingtown.bookhouse.dto.response.ExchangingBookResponse;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
import kr.co.readingtown.bookhouse.integration.book.BookReader;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import kr.co.readingtown.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookhouseService {

    private final BookReader bookReader;
    private final BookhouseRepository bookhouseRepository;

    // 서재에 책 등록
    @Transactional
    public void addBooksToBookhouse(Long memberId, BookInfoRequestDto bookInfoRequestDto) {

        Long bookId = bookReader.saveIfNotExistsAndGetBookId(bookInfoRequestDto);

        Bookhouse newBookhouse = Bookhouse.builder()
                .memberId(memberId)
                .bookId(bookId)
                .build();
        bookhouseRepository.save(newBookhouse);
    }

    // 서재에서 책 삭제
    @Transactional
    public void deleteBookFromBookhouse(Long memberId, Long bookId) {

        Bookhouse bookhouse = bookhouseRepository.findByMemberIdAndBookId(memberId, bookId)
                .orElseThrow(BookhouseException.BookhouseNotFound::new);
        bookhouseRepository.delete(bookhouse);
    }

    // 특정 회원의 서재 책 리스트 조회
    public PageResponse<BookPreviewResponseDto> getBookhouseBooks(Long memberId, int page, int size) {

        Page<Bookhouse> bookhousePage = bookhouseRepository.findAllByMemberId(memberId, PageRequest.of(page, size));

        List<Long> bookIds = bookhousePage.stream()
                .map(Bookhouse::getBookId)
                .toList();

        List<BookPreviewResponseDto> content = bookReader.getBookInfo(bookIds);

        return PageResponse.of(content, bookhousePage);
    }

    // 교환 중인 책 리스트 조회
    public List<ExchangingBookResponse> getMyExchangingBooks(Long memberId) {
        
        // EXCHANGED 상태인 내 책들 조회
        List<Bookhouse> myExchangedBooks = bookhouseRepository.findByMemberIdAndIsExchanged(memberId, IsExchanged.EXCHANGED);
        
        if (myExchangedBooks.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 채팅방별로 그룹화
        Map<Long, List<Bookhouse>> booksByChatroom = myExchangedBooks.stream()
                .filter(b -> b.getChatroomId() != null)
                .collect(Collectors.groupingBy(Bookhouse::getChatroomId));
        
        List<ExchangingBookResponse> responses = new ArrayList<>();
        
        for (Map.Entry<Long, List<Bookhouse>> entry : booksByChatroom.entrySet()) {
            Long chatroomId = entry.getKey();
            Bookhouse myBook = entry.getValue().get(0); // 채팅방당 내 책은 1개
            
            // 같은 채팅방의 상대방 책 찾기 (채팅방에는 2개의 책만 존재)
            List<Bookhouse> ChatroomBooks = bookhouseRepository.findAllByChatroomId(chatroomId);
            Bookhouse partnerBook = ChatroomBooks.stream()
                    .filter(b -> !b.getMemberId().equals(memberId))
                    .findFirst()
                    .orElse(null);
            
            // 책 정보 조회
            BookPreviewResponseDto myBookInfo = bookReader.getBookInfo(List.of(myBook.getBookId())).get(0);
            BookPreviewResponseDto partnerBookInfo = null;
            
            if (partnerBook != null) {
                partnerBookInfo = bookReader.getBookInfo(List.of(partnerBook.getBookId())).get(0);
            }
            
            // Response 생성
            ExchangingBookDetail myBookDetail = new ExchangingBookDetail(
                    myBook.getBookhouseId(),
                    myBookInfo.bookName(),
                    myBookInfo.bookImage()
            );
            
            ExchangingBookDetail yourBookDetail = partnerBookInfo != null 
                    ? new ExchangingBookDetail(
                        partnerBook.getBookhouseId(),
                        partnerBookInfo.bookName(),
                        partnerBookInfo.bookImage()
                    )
                    : new ExchangingBookDetail(null, null, null);
            
            responses.add(new ExchangingBookResponse(chatroomId, myBookDetail, yourBookDetail));
        }

        return responses;
    }
}
