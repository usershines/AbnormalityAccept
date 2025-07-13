package com.abnormality.abnormalityaccept.entity;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Data
public class Email {
    private Integer state;
    private Integer senderId;
    private Integer receiverId;
    private String theme;
    private String content;
}
