package kr.co.readingtown.book.service;

import kr.co.readingtown.book.domain.Book;
import kr.co.readingtown.book.dto.query.BookInfoDto;
import kr.co.readingtown.book.dto.query.BookPreviewDto;
import kr.co.readingtown.book.dto.request.BookInfoRequestDto;
import kr.co.readingtown.book.dto.response.BookPreviewResponseDto;
import kr.co.readingtown.book.dto.response.BookResponseDto;
import kr.co.readingtown.book.dto.response.ChatBookResponseDto;
import kr.co.readingtown.book.exception.BookException;
import kr.co.readingtown.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    public boolean getBookExists(Long bookId) {

        if (bookId == null)
            return (false);
        return bookRepository.existsById(bookId);
    }

    // 책 기본 정보 조회
    public BookResponseDto getBookInfo(Long bookId) {

        BookInfoDto bookInfoDto = bookRepository.findBookInfoById(bookId)
                .orElseThrow(BookException.BookNotFound::new);

        return BookResponseDto.toBookResponseDto(bookInfoDto);
    }

    // id에 해당하는 책의 프리뷰 정보 조회
    public List<BookPreviewResponseDto> getBooksPreview(List<Long> bookIds) {

        List<BookPreviewDto> books = bookRepository.findAllPreviewByIds(bookIds);
        return books.stream()
                .map(BookPreviewResponseDto::toBookPreviewResponseDto)
                .toList();
    }

    // ISBN에 해당하는 책이 없으면 새로 저장하고, 책 ID를 반환
    @Transactional
    public Long saveIfNotExistsAndGetBookId(BookInfoRequestDto bookInfoRequestDto) {

        return bookRepository.findByIsbn(bookInfoRequestDto.isbn())
                .orElseGet(() -> {
                    Book newBook = bookInfoRequestDto.toBookEntity();
                    bookRepository.save(newBook);
                    return newBook.getBookId();
                });
    }

    public ChatBookResponseDto getBookNameAndImage(Long bookId) {

        BookInfoDto bookInfoDto = bookRepository.findBookInfoById(bookId)
                .orElseThrow(BookException.BookNotFound::new);

        return ChatBookResponseDto.toChatBookResponseDto(bookInfoDto);
    }

    // 책 이름으로 검색
    public List<BookPreviewResponseDto> searchBooks(String bookname) {
        // 검색어에서 띄어쓰기 제거
        String searchKeyword = bookname.replaceAll("\\s+", "");
        List<BookPreviewDto> books = bookRepository.findByBookNameContaining(searchKeyword);
        return books.stream()
                .map(BookPreviewResponseDto::toBookPreviewResponseDto)
                .toList();
    }
}
