package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;


@Data
public class UserAndTeam {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long teamId; // 团队ID（关联team表）

    private Long userId; // 用户ID（关联user表）

}