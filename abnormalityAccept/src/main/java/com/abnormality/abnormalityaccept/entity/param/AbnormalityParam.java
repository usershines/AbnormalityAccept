package com.abnormality.abnormalityaccept.entity.param;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-19
 */
@Data
public class AbnormalityParam {
    private Long id;
    private String name;
    private Integer level;
    private String description;
    private String manageMethod;
    private String notes;
    private Long facilityId;

    //范围查询参数
    private Integer minLevel;
    private Integer maxLevel;

    private Integer pageNUm;
    private Integer pageSize;

}
