package com.zs.service;

import com.zs.dto.PagedResponse;
import com.zs.dto.ReviewItemResponse;
import com.zs.entity.Review;
import com.zs.entity.User;
import com.zs.repository.BookRepository;
import com.zs.repository.ReviewRepository;
import com.zs.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(User user, String oldPassword, String newPassword) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User updateProfile(User user, String nickname, String avatarUrl) {
        if (nickname != null && !nickname.isBlank()) {
            user.setNickname(nickname);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        return userRepository.save(user);
    }

    public PagedResponse<ReviewItemResponse> getMyReviews(User user, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewPage = reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

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

        return new PagedResponse<>(
                items,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isLast()
        );
    }

    @Transactional
    public Review updateReview(User user, Long reviewId, Integer newRating, String newContent) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("评价不存在"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权修改他人评价");
        }

        if (newRating != null && !newRating.equals(review.getRating())) {
            int oldRating = review.getRating();
            review.getBook().setRating(review.getBook().getRating() - oldRating + newRating);
            review.setRating(newRating);
        }

        if (newContent != null && !newContent.isBlank()) {
            review.setContent(newContent);
        }

        return reviewRepository.save(review);
    }
}
