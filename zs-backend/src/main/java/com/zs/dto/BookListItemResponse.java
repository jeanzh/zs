package com.zs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class BookListItemResponse {
    private Long id;
    private String title;
    private String author;
    private String coverUrl;
    private List<String> tags;
    private String summary;
    private Integer rating;
    private Integer reviewCount;
    private double avgRating;
    private String serialStatus;
    private Long totalWords;
    private String latestChapterTitle;
    private String bookUrl;
    private String latestUpdateUrl;
    private LocalDateTime createdAt;
}
