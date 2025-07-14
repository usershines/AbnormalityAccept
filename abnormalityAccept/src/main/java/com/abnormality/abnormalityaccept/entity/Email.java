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
public class Email {
    private Long id;
    private Integer state;
    private Integer senderId;
    private Integer receiverId;
    private String theme;
    private String content;
}
