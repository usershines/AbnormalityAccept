package com.abnormality.abnormalityaccept.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private Long inviterId;
    private Long leaderId;
    private Long facilityId;
    private String introduction ;
    private Integer level;

}
