package com.zs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 32)
    private String nickname;

    @Size(min = 11, max = 11, message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64)
    private String password;
}
