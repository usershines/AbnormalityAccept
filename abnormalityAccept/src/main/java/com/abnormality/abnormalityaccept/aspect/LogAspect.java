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
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 日志切面类，用于记录控制器层的请求日志和异常处理
 * <p>该切面通过AOP环绕通知方式，对控制器层所有方法进行拦截，实现请求信息记录、
 * 执行时间统计、正常响应日志记录以及异常捕获与日志发布功能
 * @author poyuan
 */
@Aspect
@Component
@Slf4j
public class LogAspect {

    /**
     * Spring应用上下文对象，用于发布事件
     * <p>主要用于在捕获异常时发布{@link ExceptionLogEvent}事件
     */
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AuthHandler authHandler;

    @Pointcut("execution(* com.abnormality.abnormalityaccept.controller.*.*(..))")
    public void log(){

    }

    /**
     * 环绕通知方法，用于处理控制器方法的执行过程
     * <p>该方法会在目标控制器方法执行前后进行拦截，实现以下功能：
     * <ul>
     *   <li>记录请求开始时间，计算方法执行耗时</li>
     *   <li>获取请求上下文信息（URL、方法名、参数等）</li>
     *   <li>执行目标方法并记录正常响应日志</li>
     *   <li>捕获方法执行过程中的异常，记录异常信息并发布异常日志事件</li>
     *   <li>无论方法正常执行还是异常，均记录方法执行耗时</li>
     * </ul>
     * @param joinPoint 连接点对象，包含目标方法的信息和参数
     * @return 目标方法的执行结果，封装为{@link Result}对象
     */
    @Around("log()")
    public Result<?> controller(ProceedingJoinPoint joinPoint)  {
        long startTime = System.currentTimeMillis();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String url= null;
        if (request != null) {
            url = request.getRequestURI();
        }
        String methodName = method.getName();

        //鉴权部分
        List<String> nonAuthMethodName=Arrays.asList("login","register");
        if(!nonAuthMethodName.contains(methodName)){
            String token= request.getHeader("Authorization");
            if(StrUtil.isNotBlank(token)&&token.startsWith("Bearer ")){
                token=token.substring("Bearer ".length());
            }
            log.info("token: " + token);
            try{
                boolean passed=authHandler.verify(token);
            } catch (Exception e) {
                log.error("令牌验证失败", e);
                log.info("耗时{}", System.currentTimeMillis()-startTime);
                if(e instanceof BaseException){
                    return Result.error(((BaseException) e).getCode().getCode(),((BaseException) e).getMsg(),((BaseException) e).getMsgList());
                }
                return Result.error(Code.ERROR.getCode(),"系统错误: " + e.getMessage());
            }

        }

        //业务逻辑执行部分
        try{
            Result result =(Result) joinPoint.proceed();
            log.info("请求 URL: {}, 方法: {},参数：{}, 耗时: {}ms, 返回值: {}", url, methodName, Arrays.toString(joinPoint.getArgs()) ,System.currentTimeMillis() - startTime, result);
            return result;
        } catch (Throwable e) {

            // 异常处理逻辑
            log.error("请求 URL: {}, 方法: {}, 发生异常: {}", url, methodName, e.getMessage());
            ExceptionLog exceptionLog = new ExceptionLog();
            exceptionLog.setExceptionInfo(Arrays.toString(e.getStackTrace()));
            String machineId="";
            try{
                machineId= InetAddress.getLocalHost().getHostName();
            }catch (Exception ex){
                machineId="unknown";
            }
            exceptionLog.setMachineId(machineId);
            if(e instanceof BaseException){
                exceptionLog.setMessage(Arrays.toString(((BaseException) e).getMsgList().toArray()));
            }else{
                exceptionLog.setMessage(e.getMessage());
            }

            exceptionLog.setIp(request.getRemoteAddr());
            exceptionLog.setMethod(method.getName());
            exceptionLog.setUrl(request.getRequestURI());
            exceptionLog.setUrl(request.getRequestURI());
            exceptionLog.setUserAgent(request.getHeader("User-Agent"));
            Object[] args= joinPoint.getArgs();
            exceptionLog.setParams(Arrays.toString(args));
            exceptionLog.setUsername(request.getRemoteUser());
            applicationContext.publishEvent(new ExceptionLogEvent(exceptionLog));
            log.info(exceptionLog.toString());
            log.error(exceptionLog.getExceptionInfo());
            log.error(exceptionLog.getMessage());
            e.printStackTrace();
            if(e instanceof BaseException){
                return Result.error(((BaseException) e).getCode().getCode(), ((BaseException) e).getCode().getMsg(),e.getMessage());
            }else{
                return Result.error(e.getMessage());
            }

        }finally {
            long endTime = System.currentTimeMillis();
            log.info("耗时：" + (endTime - startTime));
            Object obj=new Object();
            obj.toString();
        }
    }
}
