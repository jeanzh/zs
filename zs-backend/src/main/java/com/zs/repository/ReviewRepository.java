package com.zs.repository;

import com.zs.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByBookId(Long bookId, Pageable pageable);
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Optional<Review> findByUserIdAndBookId(Long userId, Long bookId);
    long countByBookId(Long bookId);
}
