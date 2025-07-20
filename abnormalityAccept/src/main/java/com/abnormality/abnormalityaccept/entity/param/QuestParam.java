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
public class QuestParam {
    private Long id;
    private String questCode;
    private String questName;
    private Integer questLevel;
    private Long resolvingByTeamId;
    private Integer resolvingByTeamName;
    private String questDescription;
    private Integer state;

    //多值查询
    private List<Integer> stateList;

    //范围查询参数
    private Integer minQuestLevel;
    private Integer maxQuestLevel;

    private Integer pageNum;
    private Integer pageSize;
}
