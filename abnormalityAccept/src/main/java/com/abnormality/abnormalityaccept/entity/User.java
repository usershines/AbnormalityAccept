package com.abnormality.abnormalityaccept.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User  {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Integer level=1;
    private String teamName;
    private Long inviterId;
    private Long leaderId;
    private Long facilityId;
    private String introduction ;
    private Integer isActive;




}
