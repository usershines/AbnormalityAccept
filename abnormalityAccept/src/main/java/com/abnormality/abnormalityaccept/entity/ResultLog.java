package com.abnormality.abnormalityaccept.entity;

import com.abnormality.abnormalityaccept.dto.Result;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResultLog {
    private String id;
    private String url;
    private String method;
    private String body;
    private String machineId;
    private String userAgent;
    private String ip;

    private Result<?> result;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
