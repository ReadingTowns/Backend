package kr.co.readingtown.book.service;

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

        return bookRepository.existsById(bookId);
    }
}
