package com.abnormality.abnormalityaccept.aspect;

import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.log.ResultLog;
import com.abnormality.abnormalityaccept.event.ResultLogEvent;
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
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
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

            ResultLog rlog = new ResultLog();
            String body = "unknown";
            if (request instanceof ContentCachingRequestWrapper) {
                body = ((ContentCachingRequestWrapper) request).getContentAsString();
                log.info("请求体: {}", body);
            }
            rlog.setBody(body);
            rlog.setIp(request.getRemoteAddr());
            rlog.setMethod(methodName);
            rlog.setUrl(request.getRequestURI());
            rlog.setUserAgent(request.getHeader("User-Agent"));
            String machineId = "";
            try {
                machineId = InetAddress.getLocalHost().getHostName();
            } catch (Exception ex) {
                machineId = "unknown";
            }
            rlog.setMachineId(machineId);
            rlog.setResult(JSONUtil.toJsonStr(result));
            applicationContext.publishEvent(new ResultLogEvent(rlog));
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
