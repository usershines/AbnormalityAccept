package com.abnormality.abnormalityaccept.aspect;

import cn.hutool.core.util.StrUtil;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.ExceptionLog;
import com.abnormality.abnormalityaccept.enums.Code;
import com.abnormality.abnormalityaccept.event.ExceptionLogEvent;
import com.abnormality.abnormalityaccept.exception.BaseException;
import com.abnormality.abnormalityaccept.handler.AuthHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Aspect
@Component
@Slf4j
@Order(1)
public class LogAspect {


    @Autowired
    private ApplicationContext applicationContext;


    /**
     * 定义切点，匹配控制器包下的所有方法。
     */
    @Pointcut("execution(* com.abnormality.abnormalityaccept.controller.*.*(..))")
    public void log() {
        // 仅作为切点声明使用
    }

    @Around("log()")
    public Result<?> controller(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes)
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        String url = null;
        if (request != null) {
            url = request.getRequestURI();
        }
        String methodName = method.getName();

        // 获取请求头中的 Token 并进行鉴权，排除 login 和 register 方法

        // 执行目标方法并记录响应结果
        try {
            Result result = (Result) joinPoint.proceed();
            log.info("请求 URL: {}, 方法: {}, 参数：{}, 耗时: {}ms, 返回值: {}", url, methodName,
                    Arrays.toString(joinPoint.getArgs()), System.currentTimeMillis() - startTime, result);
            //TODO: 添加返回结果记录
            return result;
        } catch (Throwable e) {
            // 捕获异常并记录详细日志
            log.error("请求 URL: {}, 方法: {}, 发生异常: {}", url, methodName, e.getMessage());


            // 发布异常日志事件

            // 输出日志内容便于调试
//            e.printStackTrace();

            // 返回统一异常响应
//            if (e instanceof BaseException) {
//                return Result.error(((BaseException) e).getCode().getCode(),
//                        ((BaseException) e).getCode().getMsg(), e.getMessage());
//            } else {
//                return Result.error(e.getMessage());
//            }
            throw e;
        } finally {
            // 记录最终执行耗时
            long endTime = System.currentTimeMillis();
            log.info("耗时：" + (endTime - startTime));
        }
    }
}
