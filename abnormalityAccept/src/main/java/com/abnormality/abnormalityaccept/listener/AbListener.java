package com.abnormality.abnormalityaccept.listener;

import com.abnormality.abnormalityaccept.entity.ExceptionLog;
import com.abnormality.abnormalityaccept.event.ExceptionLogEvent;
import com.abnormality.abnormalityaccept.mapper.ExceptionLogMappper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AbListener {


    @Autowired
    private ExceptionLogMappper exceptionLogMapper;
    @Async
    @EventListener(ExceptionLogEvent.class)
    public void saveExceptionLog(ExceptionLogEvent event) {
        // 打印日志信息，提示正在保存异常日志
        System.out.println("保存异常日志");

        // 获取事件源（异常日志对象），并调用Mapper插入数据库
        exceptionLogMapper.insert((ExceptionLog) event.getSource());
    }
}
