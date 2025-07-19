package com.abnormality.abnormalityaccept.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  {
    @TableId(type = IdType.AUTO)
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
    private Integer isActive=1;

}
