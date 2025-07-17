package com.abnormality.abnormalityaccept.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-17
 */
@Data
public class RegistRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    @NotBlank(message = "邮箱不能为空")
    private String email;
}
