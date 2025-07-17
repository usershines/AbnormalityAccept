package com.abnormality.abnormalityaccept.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Equipment {
    private Long id;
    private Integer type;
    private String name;
    private Integer state;
    private String applicationRequirement;
    private Long masterId;
    private String description;
    private Integer pageNum;
    private Integer pageSize;
}
