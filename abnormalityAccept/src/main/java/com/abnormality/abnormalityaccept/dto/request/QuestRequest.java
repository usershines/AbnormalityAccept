package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-20
 */
@Data
public class QuestRequest {
    private String questCode;
    private String questName;
    private Integer questLevel;
    private Long resolvingByTeamId;
    private String resolvingByTeamName;
    private String questDescription;
    private Integer state;
}
