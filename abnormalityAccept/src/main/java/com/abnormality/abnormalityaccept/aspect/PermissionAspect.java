package com.abnormality.abnormalityaccept.aspect;

import com.abnormality.abnormalityaccept.annotation.Level;
import com.abnormality.abnormalityaccept.entity.JwtPayload;
import com.abnormality.abnormalityaccept.entity.User;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.service.UserService;
import com.abnormality.abnormalityaccept.util.AopUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@Order(3)
public class PermissionAspect {


    @Autowired
    private UserService userService;

    @Pointcut("@annotation(com.abnormality.abnormalityaccept.annotation.Level)")
    public void permission(){}

    @Before("permission()")
    public void checkPermission(JoinPoint joinPoint){
        String token = AopUtil.getToken();
        log.info("权限检查切面获取token{}",token);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Level levelAnnotation = method.getAnnotation(Level.class);
        int[] allowLevel = levelAnnotation.allowLevel();
        int minLevel = levelAnnotation.minLevel();
        int maxLevel = levelAnnotation.maxLevel();
        JwtPayload payload=JwtPayload.fromToken(token);
        User user=userService.findUserByName(payload.getUsername());
        if (allowLevel.length > 0) {
            if(!ArrayUtils.contains(allowLevel,user.getLevel())){
                log.info("用户{}执行了{}方法,权限鉴定失败，允许权限{}，用户权限{}",user.getUsername(),method,allowLevel,user.getLevel());
                throw new BaseException(Code.FORBIDDEN,"权限未在允许范围内");
            }
        }
        if(minLevel!=-1){
            if(user.getLevel()<minLevel){
                log.info("用户{}执行了{}方法,权限鉴定失败，最小权限{}，用户权限{}",user.getUsername(),method,minLevel,user.getLevel());
                throw new BaseException(Code.FORBIDDEN,"权限不足");
            }
        }

        if(maxLevel!=-1){
            if(user.getLevel()>maxLevel){
                log.info("用户{}执行了{}方法,权限鉴定失败，最大权限{}，用户权限{}",user.getUsername(),method,maxLevel,user.getLevel());
                throw new BaseException(Code.FORBIDDEN,"权限过高");
            }
        }

        log.info("用户{}执行了{}方法,权限鉴定成功",user.getUsername(),method);

    }
}
