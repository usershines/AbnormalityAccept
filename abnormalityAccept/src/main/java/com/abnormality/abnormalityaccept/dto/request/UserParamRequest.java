package com.abnormality.abnormalityaccept.dto.request;

import lombok.Data;

/**
 * @author shanh
 * @version 1.0
 * {@code @description: 请求体封装 }
 * @since 2025-07-17
 */

@Data
public class UserParamRequest {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Integer level;
    private Long teamId;
    private Long inviterId;
    private String inviterName;
    private Long leaderId;
    private String leaderName;
    private Long facilityId;
    private String facilityName;
    private String introduction ;
    private Integer isActive;

    //范围查询
    private  Integer minLevel;
    private  Integer maxLevel;

    private Integer pageNum;
    private Integer pageSize;
}
