package com.abnormality.abnormalityaccept.entity.log;

import com.abnormality.abnormalityaccept.dto.Result;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResultLog {
    private Long id;
    private String url;
    private String method;
    private String body;
    private String machineId;
    private String userAgent;
    private String ip;

    private String result;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
