package com.zs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserInfoResponse {
    private Long id;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String status;
    private LocalDateTime createdAt;
}
