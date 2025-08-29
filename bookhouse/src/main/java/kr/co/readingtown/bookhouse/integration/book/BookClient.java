package kr.co.readingtown.bookhouse.integration.book;

import kr.co.readingtown.bookhouse.dto.request.BookInfoRequestDto;
import kr.co.readingtown.bookhouse.dto.response.BookPreviewResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "bookhouse-book-client",
        url = "${server.base-uri}"
)
public interface BookClient {

    @PostMapping("/internal/books/preview")
    List<BookPreviewResponseDto> getBooksPreview(@RequestBody List<Long> bookIds);

    @PostMapping("/internal/books")
    Long saveIfNotExistsAndGetBookId(@RequestBody BookInfoRequestDto bookInfoRequestDto);

}
