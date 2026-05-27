package com.zs.controller;

import com.zs.dto.AddChapterRequest;
import com.zs.entity.Book;
import com.zs.entity.Chapter;
import com.zs.repository.BookRepository;
import com.zs.repository.ChapterRepository;
import com.zs.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ChapterController {

    private final BookRepository bookRepository;
    private final ChapterRepository chapterRepository;
    private final FeedService feedService;

    @PostMapping("/books/{bookId}/chapters")
    public ResponseEntity<?> addChapter(@PathVariable Long bookId,
                                        @Valid @RequestBody AddChapterRequest req) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        Chapter chapter = Chapter.builder()
                .book(book)
                .seq(req.getSeq())
                .title(req.getTitle())
                .sourceUrl(req.getSourceUrl())
                .wordCount(req.getWordCount())
                .build();
        chapter = chapterRepository.save(chapter);

        feedService.generateFeeds(book, chapter);

        return ResponseEntity.ok(Map.of(
                "id", chapter.getId(),
                "seq", chapter.getSeq(),
                "title", chapter.getTitle()
        ));
    }
}
