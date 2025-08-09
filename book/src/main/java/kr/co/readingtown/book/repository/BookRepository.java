package kr.co.readingtown.book.repository;

import kr.co.readingtown.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
