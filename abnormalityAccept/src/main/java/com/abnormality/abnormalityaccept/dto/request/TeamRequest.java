package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-21
 */
@Data
public class TeamRequest {
    private String name;

    private Integer level;

    private Long leaderId;

    private String description;
}
