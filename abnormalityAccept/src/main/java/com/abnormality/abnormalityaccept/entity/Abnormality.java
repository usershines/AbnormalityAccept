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
public class Abnormality {
    private Long id;
    private String abnormalityCode;
    private String name;
    private Integer level;
    private String description;
    private String manageMethod;
    private String notes;
    private Long facilityId;

}
