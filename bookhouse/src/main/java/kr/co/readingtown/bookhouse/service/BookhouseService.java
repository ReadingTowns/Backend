package kr.co.readingtown.bookhouse.service;

import kr.co.readingtown.bookhouse.domain.Bookhouse;
import kr.co.readingtown.bookhouse.dto.request.BookInfoRequestDto;
import kr.co.readingtown.bookhouse.dto.response.BookPreviewResponseDto;
import kr.co.readingtown.bookhouse.exception.BookhouseException;
import kr.co.readingtown.bookhouse.integration.book.BookReader;
import kr.co.readingtown.bookhouse.repository.BookhouseRepository;
import kr.co.readingtown.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
