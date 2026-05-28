package com.zs.controller;

import com.zs.dto.*;
import com.zs.entity.Book;
import com.zs.entity.Follow;
import com.zs.entity.Review;
import com.zs.entity.User;
import com.zs.repository.BookRepository;
import com.zs.repository.FollowRepository;
import com.zs.repository.ReviewRepository;
import com.zs.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final FollowRepository followRepository;

    @PostMapping("/submit")
    public ResponseEntity<?> submitBook(@Valid @RequestBody BookSubmitRequest req,
                                        @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        try {
            BookSubmitResponse resp = bookService.submitBook(req, user);
            return ResponseEntity.ok(resp);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable Long bookId,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "20") int size,
                                        @RequestParam(defaultValue = "newest") String sort) {
        Sort s = sort.equals("hot")
                ? Sort.by(Sort.Direction.DESC, "likes")
                : Sort.by(Sort.Direction.DESC, "createdAt");
        PageRequest pageable = PageRequest.of(page, size, s);

        Page<Review> reviewPage = reviewRepository.findByBookId(bookId, pageable);

        List<ReviewItemResponse> items = reviewPage.getContent().stream()
                .map(r -> new ReviewItemResponse(
                        r.getId(),
                        r.getBook().getId(),
                        r.getRating(),
                        r.getContent(),
                        r.getLikes(),
                        r.getSourceUrl(),
                        r.getBookTitle(),
                        r.getBookAuthor(),
                        r.getCreatedAt(),
                        r.getUser().getId(),
                        r.getUser().getNickname(),
                        r.getUser().getAvatarUrl()
                ))
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(
                items,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        ));
    }

    @PostMapping("/{bookId}/follow")
    public ResponseEntity<?> follow(@PathVariable Long bookId,
                                    @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        try {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new RuntimeException("书籍不存在"));
            if (followRepository.existsByUserIdAndBookId(user.getId(), bookId)) {
                return ResponseEntity.ok(Map.of("message", "已关注"));
            }
            Follow follow = Follow.builder().user(user).book(book).build();
            followRepository.save(follow);
            return ResponseEntity.ok(Map.of("message", "关注成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{bookId}/follow")
    public ResponseEntity<?> unfollow(@PathVariable Long bookId,
                                      @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        try {
            followRepository.findByUserIdAndBookId(user.getId(), bookId)
                    .ifPresentOrElse(
                            f -> followRepository.delete(f),
                            () -> { throw new RuntimeException("未关注此书"); }
                    );
            return ResponseEntity.ok(Map.of("message", "已取消关注"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listSubmissions(@RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findAllByOrderByCreatedAtDesc(pageable);

        List<ReviewItemResponse> items = reviewPage.getContent().stream()
                .map(r -> new ReviewItemResponse(
                        r.getId(),
                        r.getBook().getId(),
                        r.getRating(),
                        r.getContent(),
                        r.getLikes(),
                        r.getSourceUrl(),
                        r.getBookTitle(),
                        r.getBookAuthor(),
                        r.getCreatedAt(),
                        r.getUser().getId(),
                        r.getUser().getNickname(),
                        r.getUser().getAvatarUrl()
                ))
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(
                items,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        double avg = book.getCountOfReview() > 0
                ? Math.round((double) book.getRating() / book.getCountOfReview() * 10.0) / 10.0
                : 0.0;
        return ResponseEntity.ok(new BookListItemResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCoverUrl(),
                book.getTags(),
                book.getSummary(),
                book.getRating(),
                book.getCountOfReview(),
                avg,
                book.getSerialStatus().name(),
                book.getTotalWords(),
                book.getLatestChapterTitle(),
                book.getBookUrl(),
                book.getLatestUpdateUrl(),
                book.getCreatedAt()
        ));
    }

    @GetMapping("/library")
    public ResponseEntity<?> listBooks(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size,
                                       @RequestParam(required = false) String tag) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Book> bookPage = (tag != null && !tag.isBlank())
                ? bookRepository.findByTagOrderByIdDesc(tag, pageable)
                : bookRepository.findAllByOrderByIdDesc(pageable);

        List<BookListItemResponse> items = bookPage.getContent().stream()
                .map(b -> new BookListItemResponse(
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getCoverUrl(),
                        b.getTags(),
                        b.getSummary(),
                        b.getRating(),
                        b.getCountOfReview(),
                        b.getCountOfReview() > 0
                                ? Math.round((double) b.getRating() / b.getCountOfReview() * 10.0) / 10.0
                                : 0.0,
                        b.getSerialStatus().name(),
                        b.getTotalWords(),
                        b.getLatestChapterTitle(),
                        b.getBookUrl(),
                        b.getLatestUpdateUrl(),
                        b.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(
                items,
                bookPage.getNumber(),
                bookPage.getSize(),
                bookPage.getTotalElements(),
                bookPage.getTotalPages(),
                bookPage.isLast()
        ));
    }
}
