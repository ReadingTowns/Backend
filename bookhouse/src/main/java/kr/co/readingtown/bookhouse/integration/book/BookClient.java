package kr.co.readingtown.bookhouse.integration.book;

import kr.co.readingtown.bookhouse.dto.request.BookInfoRequestDto;
import kr.co.readingtown.bookhouse.dto.response.BookhouseBookResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "bookhouse-book-client",
        url = "${server.base-uri}"
)
public interface BookClient {

    @PostMapping("/internal/books/preview")
    List<BookhouseBookResponseDto> getBooksPreview(@RequestBody List<Long> bookIds);

    @PostMapping("/internal/books")
    Long saveIfNotExistsAndGetBookId(@RequestBody BookInfoRequestDto bookInfoRequestDto);

    @GetMapping("/internal/books/{bookId}/exists")
    boolean existsBook(@PathVariable Long bookId);

}
