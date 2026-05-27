package com.zs.repository;

import com.zs.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChapterRepository extends JpaRepository<Chapter, Long> {
    List<Chapter> findByBookIdOrderBySeqAsc(Long bookId);
    List<Chapter> findByBookIdOrderBySeqDesc(Long bookId);
    Chapter findTopByBookIdOrderBySeqDesc(Long bookId); // latest chapter
}
