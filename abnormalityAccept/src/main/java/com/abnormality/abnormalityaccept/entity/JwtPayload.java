package com.abnormality.abnormalityaccept.entity;

import lombok.Data;

import java.util.Map;

@Data
public class JwtPayload {

    private String username;
    private int level;


    public Map<String,String> toMap(){
        return Map.of(
                "username",username,
                "level",String.valueOf(level)
        );
    }
}

