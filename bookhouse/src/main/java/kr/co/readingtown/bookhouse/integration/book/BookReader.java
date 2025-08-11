package kr.co.readingtown.bookhouse.integration.book;

import kr.co.readingtown.bookhouse.dto.request.BookInfoRequestDto;
import kr.co.readingtown.bookhouse.dto.response.BookPreviewResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BookReader {

    private final BookClient bookClient;

    public List<BookPreviewResponseDto> getBookInfo(List<Long> bookIds) {

        if (bookIds == null || bookIds.isEmpty()) {
            return Collections.emptyList();
        }

        return bookClient.getBooksPreview(bookIds);
    }

    public Long saveIfNotExistsAndGetBookId(BookInfoRequestDto bookInfoRequestDto) {

        return bookClient.saveIfNotExistsAndGetBookId(bookInfoRequestDto);
    }
}
