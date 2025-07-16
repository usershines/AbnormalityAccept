package com.abnormality.abnormalityaccept.handler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import com.abnormality.abnormalityaccept.entity.JwtPayload;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.ServiceException;
import com.abnormality.abnormalityaccept.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuthHandler {

    @Autowired
    private UserService userService;


    public boolean verify(String token){
        if(StrUtil.isBlankIfStr(token)){
            throw new ServiceException(Code.NOT_AUTHENTICATED,"用户未登录");
        }
        JWT jwt = JWT.of(token);
        JwtPayload payload=jwt.getPayloads().toBean(JwtPayload.class);
        //此处天机额外字段验证逻辑，请仿照等级验证的写法来写
        int level = payload.getLevel();
        if(level==0){
            throw new ServiceException(Code.FORBIDDEN,"权限不足");
        }
        //最后验证正确性
        return userService.verify(token);

    }

}
