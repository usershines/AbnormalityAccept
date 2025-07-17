package com.abnormality.abnormalityaccept.handler;


import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.ExceptionLog;
import com.abnormality.abnormalityaccept.event.ExceptionLogEvent;
import com.abnormality.abnormalityaccept.exception.BaseException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

/**
 * 全局异常处理类，用于捕获和处理控制器层抛出的异常。
 * 该类使用 Spring 的 @RestControllerAdvice 注解，实现对全局异常的统一处理。
 *
 * <p>主要功能包括：</p>
 * <ul>
 *   <li>捕获 HttpMessageNotReadableException 异常并进行处理。</li>
 *   <li>记录异常日志信息。</li>
 *   <li>返回统一格式的错误响应。</li>
 * </ul>
 *
 */
@Slf4j
@RestControllerAdvice
public class ControllerAdviceHandler {

    /**
     * Spring 应用上下文，用于发布事件。
     * 通过注入 ApplicationContext，可以在发生异常时触发自定义事件。
     */
    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 处理 HTTP 消息不可读异常的方法。
     * 当客户端发送的请求参数无法被正确解析时，会抛出 HttpMessageNotReadableException。
     * 该方法捕获此异常并执行以下操作：
     * <ol>
     *   <li>记录异常日志。</li>
     *   <li>创建异常日志对象。</li>
     *   <li>发布异常日志事件。</li>
     *   <li>返回包含错误信息的响应结果。</li>
     * </ol>
     *
     * @param e 捕获到的 HttpMessageNotReadableException 异常对象
     * @return 包含错误信息的 Result 对象
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result handleException(Exception e){
        log.error(e.getMessage());

        ExceptionLog exceptionLog=ExceptionLog.fromException(e);

        applicationContext.publishEvent(new ExceptionLogEvent(exceptionLog));
        return Result.error("请求参数错误");
    }

    @ExceptionHandler(Exception.class)
    public Result handleBaseException(BaseException e){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes)
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        String methodName = "unknown";

        if (request instanceof ServletWebRequest swr) {
            HandlerMethod handlerMethod = (HandlerMethod) swr.getRequest()
                    .getAttribute("org.springframework.web.servlet.HandlerExceptionResolver.originalControllerMethod");

            if (handlerMethod != null) {
                methodName = handlerMethod.getMethod().getName();
            }
        }
        log.error("请求 URL: {}, 方法: {}, 发生异常: {}", request.getRequestURI(), methodName, e.getMessage());

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setExceptionInfo(Arrays.toString(e.getStackTrace()));

        // 获取本机主机名，标识发生异常的机器
        String machineId = "";
        try {
            machineId = InetAddress.getLocalHost().getHostName();
        } catch (Exception ex) {
            machineId = "unknown";
        }
        exceptionLog.setMachineId(machineId);

        // 设置异常消息
        if (e instanceof BaseException) {
            exceptionLog.setMessage(Arrays.toString(((BaseException) e).getMsgList().toArray()));
        } else {
            exceptionLog.setMessage(e.getMessage());
        }

        // 设置客户端 IP 地址
        exceptionLog.setIp(request.getRemoteAddr());
        // 设置方法名
        exceptionLog.setMethod(methodName);
        // 设置请求 URL
        exceptionLog.setUrl(request.getRequestURI());
        // 设置 User-Agent
        exceptionLog.setUserAgent(request.getHeader("User-Agent"));
        // 设置请求参数
        String body="unknown";
        if (request instanceof ContentCachingRequestWrapper) {
            body = ((ContentCachingRequestWrapper) request).getContentAsString();
            log.error("异常发生时请求体: {}", body);
        }
        exceptionLog.setParams(body);
        // 设置用户名（远程用户）
        exceptionLog.setUsername(request.getRemoteUser());

        // 发布异常日志事件
        applicationContext.publishEvent(new ExceptionLogEvent(exceptionLog));

        // 输出日志内容便于调试
        log.info(exceptionLog.toString());
        log.error(exceptionLog.getExceptionInfo());
        log.error(exceptionLog.getMessage());
        e.printStackTrace();

        // 返回统一异常响应
        if (e instanceof BaseException) {
            return Result.error(((BaseException) e).getCode().getCode(),
                    ((BaseException) e).getCode().getMsg(), e.getMessage());
        } else {
            return Result.error(e.getMessage());
        }
    }


}