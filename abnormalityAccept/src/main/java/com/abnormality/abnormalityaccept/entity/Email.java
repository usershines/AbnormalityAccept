package com.abnormality.abnormalityaccept.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {
    private Long id;
    private Integer state;
    private Long senderId;
    private Long receiverId;
    private String theme;
    private String content;
}
