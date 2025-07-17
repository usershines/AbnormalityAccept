package com.abnormality.abnormalityaccept.entity;

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
public class Abnormality {
    private Long id;

    private String name;

    //0:待评估 ,1,2,3,4,一共4个等级
    private Integer level;

    private String description;
    private String manageMethod;
    private String notes;
    private Long facilityId;

    /**
     * 图片名称,获取图片用
     */
    private String imgName;
    private Integer pageNum;
    private Integer pageSize;

}
