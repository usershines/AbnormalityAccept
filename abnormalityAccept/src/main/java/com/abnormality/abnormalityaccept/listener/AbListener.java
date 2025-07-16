package com.abnormality.abnormalityaccept.listener;

import com.abnormality.abnormalityaccept.entity.ExceptionLog;
import com.abnormality.abnormalityaccept.event.ExceptionLogEvent;
import com.abnormality.abnormalityaccept.mapper.ExceptionLogMappper;
import com.abnormality.abnormalityaccept.mq.LogSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 异常日志监听器，用于监听并处理系统中抛出的异常日志事件。
 *
 * <p>该类为 Spring 组件，通过 @EventListener 监听 ExceptionLogEvent 类型的事件，
 * 并使用 @Async 注解实现异步处理，提升响应性能。</p>
 */
@Component
public class AbListener {

    /**
     * 异常日志数据访问对象，用于将异常日志持久化到数据库。
     */
    @Autowired
    private ExceptionLogMappper exceptionLogMapper;

    /**
     * 日志发送器，用于异步发送异常日志至消息队列或其他存储介质。
     */
    @Autowired
    private LogSender logSender;

    /**
     * 处理异常日志事件的方法。
     *
     * <p>该方法在接收到 ExceptionLogEvent 事件后被触发，获取事件中的异常日志信息，
     * 并通过 logSender 将日志异步发送出去进行持久化处理。</p>
     *
     * <p><strong>注意：</strong>当前未启用数据库直接写入方式（已注释），如需启用请取消相应注释。</p>
     *
     * @param event 包含异常日志信息的事件对象
     */
    @Async
    @EventListener(ExceptionLogEvent.class)
    public void saveExceptionLog(ExceptionLogEvent event) {
        // 提示信息：用于调试，可删除或保留用于运行时监控
//        System.out.println("保存异常日志");

        // 获取事件源中的异常日志对象，并通过消息队列发送日志数据
        // 此处采用异步方式发送日志，避免阻塞主线程
        logSender.saveLog((ExceptionLog) event.getSource());

    }
}
