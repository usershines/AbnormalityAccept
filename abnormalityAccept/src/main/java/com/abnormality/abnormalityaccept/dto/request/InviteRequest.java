package com.abnormality.abnormalityaccept.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-16
 */
@Data
public class InviteRequest {

    @NotBlank(message = "用户名不能为空")
    private String username;

    private Long facilityId;

    @NotNull(message = "用户等级不能为空")
    @Min(value = 1, message = "等级必须大于0")
    @Max(value = 5, message = "等级不能超过5")
    private Integer level;
}
