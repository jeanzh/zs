package com.zs.controller;

import com.zs.dto.*;
import com.zs.entity.Review;
import com.zs.entity.User;
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
}
