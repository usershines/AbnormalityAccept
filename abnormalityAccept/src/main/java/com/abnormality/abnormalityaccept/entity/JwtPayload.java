package com.abnormality.abnormalityaccept.entity;

import cn.hutool.jwt.JWT;
import lombok.Data;

import java.util.Map;

@Data
public class JwtPayload {
    private Long id;
    private String username;
    private int level;
    private int expDays=7;
    public Map<String,String> toMap(){
        return Map.of(
                "id",id.toString(),
                "username",username,
                "level",String.valueOf(level)
        );
    }

    public static JwtPayload fromToken(String token){
        JWT jwt = JWT.of(token);
        return jwt.getPayloads().toBean(JwtPayload.class);
    }
}

