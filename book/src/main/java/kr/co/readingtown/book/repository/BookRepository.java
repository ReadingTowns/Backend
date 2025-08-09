package kr.co.readingtown.book.repository;

import kr.co.readingtown.book.domain.Book;
import kr.co.readingtown.book.dto.query.BookInfoDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
    SELECT new kr.co.readingtown.book.dto.query.BookInfoDto(
        b.bookName,
        b.bookImage,
        b.author,
        b.publisher,
        b.summary
    )
    FROM Book b
    WHERE b.bookId = :bookId
    """)
    Optional<BookInfoDto> findBookInfoById(@Param("bookId") Long bookId);
}
