package kr.co.readingtown.book.externalapi;

import kr.co.readingtown.book.dto.response.BookResponseDto;
import kr.co.readingtown.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class ExternalBookController {

    private final BookService bookService;

    @GetMapping("/{bookId}")
    public BookResponseDto getBookInfo(@PathVariable("bookId") Long bookId) {

        return bookService.getBookInfo(bookId);
    }
}
