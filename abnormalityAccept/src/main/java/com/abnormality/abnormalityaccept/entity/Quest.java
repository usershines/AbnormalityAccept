package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.AUTO)
    private Long id;
    private String questCode;
    private String questName;
    private Long resolvingByTeamId;
    private String resolvingByTeamName;
    private String questDescription;
    private Integer state;


}
