package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-18
 */
@Data
public class EmailAddRequest {
    private String receiverName;
    private String theme;
    private String content;
}
