package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-18
 */
@Data
public class TeamUpdateRequest {
    private Long id;
    private String name;
    private Integer status;
    private Long resolvingQuestId;
    private Integer level;
    private Long leaderId;
    private String description;
}
