package com.abnormality.abnormalityaccept.entity;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Data
public class Facility {
    private String id;
    private String facilityName;
    private String facilityAddress;
    private Integer abnormalityId;
}
