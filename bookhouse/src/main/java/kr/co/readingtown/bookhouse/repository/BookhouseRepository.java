package kr.co.readingtown.bookhouse.repository;


import kr.co.readingtown.bookhouse.domain.Bookhouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookhouseRepository extends JpaRepository<Bookhouse, Long> {

}