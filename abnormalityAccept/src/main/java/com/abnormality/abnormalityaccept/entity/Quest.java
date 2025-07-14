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
public class Quest {
    private Long id;
    private String questCode;
    private String questName;
    private String questDescription;
    private Integer state;
}
