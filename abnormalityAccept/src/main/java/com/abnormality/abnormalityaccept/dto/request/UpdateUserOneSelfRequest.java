package com.abnormality.abnormalityaccept.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-18
 */
@Data
public class UpdateUserOneSelfRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;

    private String email;

    private String introduction;



}
