package com.zs.controller;

import com.zs.dto.*;
import com.zs.entity.Follow;
import com.zs.entity.Review;
import com.zs.entity.User;
import com.zs.repository.FollowRepository;
import com.zs.service.FeedService;
import com.zs.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FeedService feedService;
    private final FollowRepository followRepository;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }

        String maskedPhone = user.getPhone();
        if (maskedPhone != null && maskedPhone.length() == 11) {
            maskedPhone = maskedPhone.substring(0, 3) + "****" + maskedPhone.substring(7);
        }

        return ResponseEntity.ok(new UserInfoResponse(
                user.getId(),
                user.getNickname(),
                user.getAvatarUrl(),
                maskedPhone,
                user.getStatus().name(),
                user.getCreatedAt()
        ));
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest req,
                                            @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        try {
            userService.changePassword(user, req.getOldPassword(), req.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "密码修改成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest req,
                                           @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        User updated = userService.updateProfile(user, req.getNickname(), req.getAvatarUrl());
        return ResponseEntity.ok(Map.of(
                "id", updated.getId(),
                "nickname", updated.getNickname(),
                "avatarUrl", updated.getAvatarUrl() != null ? updated.getAvatarUrl() : ""
        ));
    }

    @GetMapping("/reviews")
    public ResponseEntity<?> getMyReviews(@AuthenticationPrincipal User user,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "20") int size) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        return ResponseEntity.ok(userService.getMyReviews(user, page, size));
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId,
                                          @Valid @RequestBody UpdateReviewRequest req,
                                          @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        try {
            Review review = userService.updateReview(user, reviewId, req.getRating(), req.getContent());
            return ResponseEntity.ok(Map.of(
                    "id", review.getId(),
                    "rating", review.getRating(),
                    "content", review.getContent(),
                    "updatedAt", review.getUpdatedAt()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/feeds")
    public ResponseEntity<?> getFeeds(@AuthenticationPrincipal User user,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        return ResponseEntity.ok(feedService.getFeeds(user, page, size));
    }

    @GetMapping("/shelf")
    public ResponseEntity<?> getShelf(@AuthenticationPrincipal User user,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        PageRequest pageable = PageRequest.of(page, size);
        Page<Follow> followPage = followRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable);

        List<BookListItemResponse> items = followPage.getContent().stream()
                .map(f -> {
                    var b = f.getBook();
                    double avg = b.getCountOfReview() > 0
                            ? Math.round((double) b.getRating() / b.getCountOfReview() * 10.0) / 10.0
                            : 0.0;
                    return new BookListItemResponse(
                            b.getId(), b.getTitle(), b.getAuthor(), b.getCoverUrl(),
                            b.getTags(), b.getSummary(), b.getRating(), b.getCountOfReview(),
                            avg, b.getSerialStatus().name(), b.getTotalWords(),
                            b.getLatestChapterTitle(), b.getBookUrl(), b.getLatestUpdateUrl(),
                            b.getCreatedAt());
                })
                .toList();

        return ResponseEntity.ok(new PagedResponse<>(
                items,
                followPage.getNumber(),
                followPage.getSize(),
                followPage.getTotalElements(),
                followPage.getTotalPages(),
                followPage.isLast()
        ));
    }

    @DeleteMapping("/feeds/{feedId}")
    public ResponseEntity<?> dismissFeed(@PathVariable Long feedId,
                                         @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(401).body(Map.of("error", "请先登录"));
        }
        try {
            feedService.dismissFeed(user, feedId);
            return ResponseEntity.ok(Map.of("message", "已清除"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
