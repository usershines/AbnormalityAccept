package com.abnormality.abnormalityaccept.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id;
    private String username;
    private String password;
    private String email;
    private Integer inviterId;
    private Integer leaderId;
    private Integer facilityId;
    private String introduction ;
    private Integer level;

}
