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
public class Facility {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String facilityName;
    private String facilityAddress;
    private Integer level;
    private Long managerId;
    private String managerName;

    //用于表示是否启用
    private Integer isActive=1;

    
}
