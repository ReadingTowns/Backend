package kr.co.readingtown.chat.integration.book;

import kr.co.readingtown.chat.dto.response.BookInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "chat-book-client",
        url = "${server.base-uri}"
)
public interface BookClient {

    @GetMapping("/internal/books/{bookId}")
    BookInfoResponse getBookInfo(@PathVariable Long bookId);
}
