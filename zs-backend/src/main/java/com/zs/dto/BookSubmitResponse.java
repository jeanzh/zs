package com.zs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BookSubmitResponse {
    private Long bookId;
    private String title;
    private String author;
    private List<String> tags;
    private Integer rating;
    private Integer reviewCount;
    private double avgRating;
    private String bookUrl;
    private String latestUpdateUrl;
    private boolean newlyCreated;
    private Long reviewId;
}
