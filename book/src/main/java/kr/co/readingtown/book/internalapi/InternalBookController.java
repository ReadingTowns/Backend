package kr.co.readingtown.book.internalapi;

import kr.co.readingtown.book.dto.request.BookInfoRequestDto;
import kr.co.readingtown.book.dto.response.BookPreviewResponseDto;
import kr.co.readingtown.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/books")
public class InternalBookController {

    private final BookService bookService;

    @GetMapping("/{bookId}/exists")
    public boolean existsBook(@PathVariable("bookId") Long bookId) {

        return bookService.getBookExists(bookId);
    }

    @PostMapping("/preview")
    public List<BookPreviewResponseDto> getBooksPreview(@RequestBody List<Long> bookIds) {

        return bookService.getBooksPreview(bookIds);
    }

    @PostMapping
    public Long saveIfNotExistsAndGetBookId(@RequestBody BookInfoRequestDto bookInfoRequestDto) {

        return bookService.saveIfNotExistsAndGetBookId(bookInfoRequestDto);
    }
}
