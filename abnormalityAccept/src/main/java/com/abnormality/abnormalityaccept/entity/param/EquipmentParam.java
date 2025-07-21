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
public class EquipmentParam {
    private Long id;
    private Integer type;
    private String name;
    private Integer state;
    private String applicationRequirement;
    private Long masterId;
    private String description;

    //多值查询参数
    private List<Integer> stateList;
    private List<Integer> typeList;

    private Integer pageNum;
    private Integer pageSize;

}
