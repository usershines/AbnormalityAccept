package com.abnormality.abnormalityaccept.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.AUTO)
    private Long id;
    private Integer state;
    private Long senderId;
    private String senderName;
    private Integer senderLevel;
    private Long receiverId;
    private String receiverName;
    private Integer receiverLevel;
    private String theme;
    private String content;
    private LocalDate sendTime;

}
