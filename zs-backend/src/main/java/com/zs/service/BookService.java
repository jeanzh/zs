package com.zs.service;

import com.zs.dto.BookSubmitRequest;
import com.zs.dto.BookSubmitResponse;
import com.zs.entity.*;
import com.zs.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final FollowRepository followRepository;

    @Transactional
    public BookSubmitResponse submitBook(BookSubmitRequest req, User user) {
        boolean newlyCreated = false;
        Book book = bookRepository
                .findByTitleAndAuthor(req.getTitle(), req.getAuthor())
                .orElse(null);

        if (book == null) {
            book = Book.builder()
                    .title(req.getTitle())
                    .author(req.getAuthor())
                    .tags(req.getTags())
                    .summary(req.getSummary())
                    .worldBackground(req.getWorldBackground())
                    .highlights(req.getHighlights())
                    .bookUrl(req.getBookUrl())
                    .latestUpdateUrl(req.getLatestUpdateUrl())
                    .serialStatus(SerialStatus.ONGOING)
                    .build();
            book = bookRepository.save(book);
            newlyCreated = true;
        }

        // Always create new review
        Review review = Review.builder()
                .user(user)
                .book(book)
                .rating(req.getRating())
                .content(req.getReviewContent())
                .sourceUrl(req.getSourceUrl())
                .bookTitle(book.getTitle())
                .bookAuthor(book.getAuthor())
                .build();
        reviewRepository.save(review);

        // Cumulative rating: add this review's rating to the book's total
        book.setRating(book.getRating() + req.getRating());
        book.setCountOfReview(book.getCountOfReview() + 1);
        bookRepository.save(book);

        // Auto-follow
        if (!followRepository.existsByUserIdAndBookId(user.getId(), book.getId())) {
            followRepository.save(Follow.builder()
                    .user(user)
                    .book(book)
                    .build());
        }

        double avg = book.getCountOfReview() > 0
                ? (double) book.getRating() / book.getCountOfReview()
                : 0.0;

        return new BookSubmitResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getTags(),
                book.getRating(),
                book.getCountOfReview(),
                Math.round(avg * 10.0) / 10.0,
                book.getBookUrl(),
                book.getLatestUpdateUrl(),
                book.getLatestChapterTitle(),
                newlyCreated,
                review.getId()
        );
    }
}
