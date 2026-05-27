package com.zs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddChapterRequest {
    @NotNull
    private Integer seq;

    @NotBlank
    private String title;

    private String sourceUrl;
    private Integer wordCount;
}
