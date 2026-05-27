package com.zs.controller;

import com.zs.dto.*;
import com.zs.entity.Review;
import com.zs.entity.User;
import com.zs.service.FeedService;
import com.zs.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FeedService feedService;

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
