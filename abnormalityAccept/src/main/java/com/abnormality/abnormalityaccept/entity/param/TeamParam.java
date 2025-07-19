package com.abnormality.abnormalityaccept.entity.param;

import lombok.Data;

import java.util.List;

/**
 * @author shanh
 * @version 1.0
 * {@code @description:}
 * @since 2025-07-19
 */
@Data
public class TeamParam {
    private Long id;
    private String name;
    private Integer status;
    private Long resolvingQuestId;
    private Integer level;
    private String description;
    private Long leaderId;
    private Integer isActive=1;

    //范围查询参数
    private Integer minLevel;
    private Integer maxLevel;

    //多值查询参数
    private List<Integer> statusList;

    private Integer pageNum;
    private Integer pageSize;
}
