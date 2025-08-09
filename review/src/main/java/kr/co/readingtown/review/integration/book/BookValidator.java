package kr.co.readingtown.review.integration.book;

import kr.co.readingtown.review.exception.ReviewException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookValidator {

    private final BookClient bookClient;

    // bookId 존재하는지 확인
    public void validateBookExists(Long bookId) {

        boolean exists = bookClient.existsBook(bookId);
        if (!exists) {
            throw new ReviewException.ReviewBookNotFound();
        }
    }
}
