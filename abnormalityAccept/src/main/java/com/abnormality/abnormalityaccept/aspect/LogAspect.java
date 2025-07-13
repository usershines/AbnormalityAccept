package com.abnormality.abnormalityaccept.aspect;

import com.abnormality.abnormalityaccept.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {


    @Autowired
    private ApplicationContext applicationContext;
    @Pointcut("execution(* com.abnormality.abnormalityaccept.controller.*.*(..))")
    public void log(){

    }

    @Around("log()")
    public Result<?> controller(ProceedingJoinPoint joinPoint)  {
        try{
            Result result= (Result) joinPoint.proceed();
            log.info("{}",result);
            return result;
        } catch (Throwable e) {
            return null;
        }
    }
}
