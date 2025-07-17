package com.abnormality.abnormalityaccept.entity;

import lombok.Data;

@Data
public class TeamMember {
    private Long id;

    private Long teamId; // 团队ID（关联team表）

    private Long userId; // 用户ID（关联user表）
}