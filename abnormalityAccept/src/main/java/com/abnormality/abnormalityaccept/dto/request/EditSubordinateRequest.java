package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-18
 */
@Data
public class EditSubordinateRequest {
    private Long subordinateId;

    @NotBlank(message = "等级不能为空")
    private Integer level;

    private String leaderName;


}
