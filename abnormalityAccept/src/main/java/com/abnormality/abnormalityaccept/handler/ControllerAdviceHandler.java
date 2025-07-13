package com.abnormality.abnormalityaccept.handler;


import com.abnormality.abnormalityaccept.dto.Result;
import com.abnormality.abnormalityaccept.entity.ExceptionLog;
import com.abnormality.abnormalityaccept.event.ExceptionLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
 * @author poyuan
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
}