package com.abnormality.abnormalityaccept.entity.param;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-19
 */
@Data
public class FacilityParam {
    private Long id;
    private String facilityName;
    private String facilityAddress;
    private Integer level;
    private Long managerId;
    private String managerName;

    //范围查询参数
    private Integer minLevel;
    private Integer maxLevel;

    private Integer pageNum;
    private Integer pageSize;
}
