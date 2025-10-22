package kr.co.readingtown.book.externalapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.readingtown.book.dto.response.BookPreviewResponseDto;
import kr.co.readingtown.book.dto.response.BookResponseDto;
import kr.co.readingtown.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@Tag(name = "Book API", description = "도서 관련 API")
public class ExternalBookController {

    private final BookService bookService;

    @GetMapping("/{bookId}")
    @Operation(summary = "책 정보 조회", description = "특정 책의 상세 정보를 조회합니다.")
    public BookResponseDto getBookInfo(@PathVariable("bookId") Long bookId) {

        return bookService.getBookInfo(bookId);
    }

    @GetMapping("/search")
    @Operation(summary = "책 검색", description = "책 이름으로 도서를 검색합니다. 입력한 키워드가 포함된 모든 책을 반환합니다.")
    public List<BookPreviewResponseDto> searchBooks(@RequestParam("bookname") String bookname) {
        return bookService.searchBooks(bookname);
    }
}
