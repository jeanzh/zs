package com.zs.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookSubmitRequest {

    @NotBlank(message = "书名不能为空")
    @Size(max = 256)
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 128)
    private String author;

    private List<String> tags;

    @Size(max = 2000)
    private String summary;

    @Size(max = 2000)
    private String worldBackground;

    @Size(max = 2000)
    private String highlights;

    @NotNull(message = "评分不能为空")
    @Min(1) @Max(5)
    private Integer rating;

    private String sourceUrl;

    private String bookUrl;

    private String latestUpdateUrl;

    @NotBlank(message = "评价内容不能为空")
    @Size(min = 10, max = 2000, message = "评价内容至少10个字")
    private String reviewContent;
}
