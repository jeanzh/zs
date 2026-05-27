package com.zs.repository;

import com.zs.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Follow> findByBookId(Long bookId);
    Optional<Follow> findByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    long countByBookId(Long bookId);
}
