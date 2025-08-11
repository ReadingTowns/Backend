package kr.co.readingtown.book.repository;

import kr.co.readingtown.book.domain.Book;
import kr.co.readingtown.book.dto.query.BookInfoDto;
import kr.co.readingtown.book.dto.query.BookPreviewDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    @Query("""
    SELECT b.bookId
    FROM Book b
    WHERE b.isbn = :isbn
    """)
    Optional<Long> findByIsbn(@Param("isbn") String isbn);

    @Query("""
    SELECT new kr.co.readingtown.book.dto.query.BookPreviewDto(
        b.bookId,
        b.bookImage,
        b.bookName,
        b.author
    )
    FROM Book b
    WHERE b.bookId IN :bookIds
    """)
    List<BookPreviewDto> findAllPreviewByIds(@Param("bookIds") List<Long> bookIds);
}
