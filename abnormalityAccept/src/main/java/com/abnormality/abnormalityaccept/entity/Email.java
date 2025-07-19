package com.abnormality.abnormalityaccept.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



import lombok.Data;
import org.apache.poi.hpsf.Date;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String theme;
    private String content;
    private LocalDate sendTime;

}
