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
public class Facility {
    private Long id;
    private String facilityName;
    private String facilityAddress;
    private Long[] abnormalityIds;
}
