package com.abnormality.abnormalityaccept.aspect;

import cn.hutool.core.util.StrUtil;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.handler.AuthHandler;
import com.abnormality.abnormalityaccept.util.AopUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@Order(2)
public class AuthAspect {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AuthHandler authHandler;

    @Pointcut("!@annotation(com.abnormality.abnormalityaccept.annotation.AuthIgnore) && execution(* com.abnormality.abnormalityaccept.controller.*.*(..))")
    public void auth(){}

    @Before("auth()")
    public void checkAuth(JoinPoint joinPoint){
        long startTime=System.currentTimeMillis();
        String token= AopUtil.getToken();
        log.info("登录切面获取token{}",token);

        log.info("token: " + token);

        try {
//            AuthHandler authHandler = applicationContext.getBean(AuthHandler.class);
            boolean passed = authHandler.verify(token);
        } catch (Exception e) {
            log.error("令牌验证失败", e);
            log.info("耗时{}", System.currentTimeMillis() - startTime);
            throw new BaseException(Code.NOT_AUTHORIZED, "登录令牌验证失败",e);
        }

    }
}
