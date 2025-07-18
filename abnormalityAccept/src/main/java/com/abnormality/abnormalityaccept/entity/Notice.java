package com.abnormality.abnormalityaccept.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    private Long id;
    private String theme;
    private String content;
    private Date time;
    private Integer state;

}
