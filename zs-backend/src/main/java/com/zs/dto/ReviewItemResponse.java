package com.zs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReviewItemResponse {
    private Long id;
    private Integer rating;
    private String content;
    private Integer likes;
    private String sourceUrl;
    private String bookTitle;
    private String bookAuthor;
    private LocalDateTime createdAt;
    private Long userId;
    private String userNickname;
    private String userAvatarUrl;
}
