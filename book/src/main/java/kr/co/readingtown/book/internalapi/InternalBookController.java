package kr.co.readingtown.book.internalapi;

import kr.co.readingtown.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/books")
public class InternalBookController {

    private final BookService bookService;

    @GetMapping("/{bookId}/exists")
    public boolean existsBook(@PathVariable("bookId") Long bookId) {

        return bookService.getBookExists(bookId);
    }
}
