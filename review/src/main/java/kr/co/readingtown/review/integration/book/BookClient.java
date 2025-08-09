package kr.co.readingtown.review.integration.book;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "review-book-client",
        url = "${server.base-uri}"
)
public interface BookClient {

    @GetMapping("/internal/books/{bookId}/exists")
    boolean existsBook(@PathVariable("bookId") Long bookId);
}
