package com.abnormality.abnormalityaccept.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String name;
    private String password;
    private Integer level;
    private String email;
    private Long facilityId;
    //上级id
    private Long superiorId;
}
