package com.zs.controller;

import com.zs.dto.UserInfoResponse;
import com.zs.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

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
}
