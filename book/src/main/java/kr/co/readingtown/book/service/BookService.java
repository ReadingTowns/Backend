package kr.co.readingtown.book.service;

import kr.co.readingtown.book.dto.query.BookInfoDto;
import kr.co.readingtown.book.dto.response.BookResponseDto;
import kr.co.readingtown.book.exception.BookException;
import kr.co.readingtown.book.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
