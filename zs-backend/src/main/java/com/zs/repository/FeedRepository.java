package com.zs.repository;

import com.zs.entity.Feed;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedRepository extends JpaRepository<Feed, Long> {
    Page<Feed> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Feed> findAllByOrderByCreatedAtDesc(Pageable pageable); // global feed for guests
    boolean existsByUserIdAndBookIdAndChapterId(Long userId, Long bookId, Long chapterId);
}
