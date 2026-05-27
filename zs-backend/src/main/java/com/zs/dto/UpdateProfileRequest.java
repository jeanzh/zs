package com.zs.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(min = 2, max = 32)
    private String nickname;

    @Size(max = 512)
    private String avatarUrl;
}
