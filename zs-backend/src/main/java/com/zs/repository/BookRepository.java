package com.zs.repository;

import com.zs.entity.Book;
import com.zs.entity.SerialStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitleAndAuthor(String title, String author);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorContainingIgnoreCase(String author);
    List<Book> findBySerialStatus(SerialStatus status);
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    Page<Book> findAllByOrderByIdDesc(Pageable pageable);

    @Query(value = "SELECT * FROM book WHERE :tag = ANY(tags) ORDER BY id DESC",
           countQuery = "SELECT count(*) FROM book WHERE :tag = ANY(tags)",
           nativeQuery = true)
    Page<Book> findByTagOrderByIdDesc(String tag, Pageable pageable);
}
