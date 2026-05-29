package com.zs.repository;

import com.zs.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<Follow> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    List<Follow> findByBookId(Long bookId);
    Optional<Follow> findByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    long countByBookId(Long bookId);
}
