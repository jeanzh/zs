package com.zs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FeedItemResponse {
    private Long feedId;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String bookCoverUrl;
    private Long chapterId;
    private String chapterTitle;
    private LocalDateTime createdAt;
}
