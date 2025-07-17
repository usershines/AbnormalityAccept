package com.abnormality.abnormalityaccept.mq;

import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.entity.log.ExceptionLog;
import com.abnormality.abnormalityaccept.entity.log.ResultLog;
import com.abnormality.abnormalityaccept.mapper.ExceptionLogMappper;
import com.abnormality.abnormalityaccept.mapper.ResultLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 日志接收器，用于监听 RabbitMQ 中的日志消息队列并处理接收到的异常日志。
 *
 * <p>该类为 Spring 组件，通过 @RabbitListener 注解监听指定队列（logQueue）中的消息，
 * 接收后将 JSON 格式的消息反序列化为 ExceptionLog 对象，并持久化到数据库。</p>
 *
 * <p>
 *     <strong>
 *         用于异步写入日志以减轻服务器压力，同时实现日志功能与具体服务器的解耦，方便多机部署。
 *     </strong>
 * </p>
 */
@Slf4j
@Component
public class LogReceiver {

    /**
     * 异常日志数据访问对象，用于将接收到的日志信息插入到数据库中。
     */
    @Autowired
    private ExceptionLogMappper exceptionLogMapper;

    @Autowired
    private ResultLogMapper resultLogMapper;

    /**
     * 接收 RabbitMQ 队列中的日志消息，并进行解析与持久化操作。
     *
     * <p>该方法的主要流程如下：</p>
     * <ul>
     *   <li>从消息队列中接收一条 JSON 格式的字符串消息。</li>
     *   <li>将消息内容转换为 ExceptionLog 实体对象。</li>
     *   <li>调用 Mapper 将异常日志写入数据库。</li>
     * </ul>
     *
     * @param message 从队列中接收到的原始日志消息，格式为 JSON 字符串
     */
    @RabbitListener(queues = "exceptionLogQueue")
    public void receiveExceptionLog(String message) {
        // 记录接收到的消息内容，便于调试和监控
        log.info("接收到日志消息：{}", message);

        // 将 JSON 格式的消息字符串转换为 ExceptionLog 对象
        ExceptionLog elog = JSONUtil.toBean(message, ExceptionLog.class);

        // 将解析后的异常日志插入数据库
        exceptionLogMapper.insert(elog);

        // 记录日志保存成功的信息，便于后续排查和审计
        log.info("保存异常日志成功{}", elog);
    }

    @RabbitListener(queues = "resultLogQueue")
    public void receiveResultLog(String message) {
        // 记录接收到的消息内容，便于调试和监控
        log.info("接收到日志消息：{}", message);

        // 将 JSON 格式的消息字符串转换为 ResultLog 对象
        ResultLog rlog = JSONUtil.toBean(message, ResultLog.class);

        // 将解析后的结果日志插入数据库
        resultLogMapper.insert(rlog);
        // 记录日志保存成功的信息，便于后续排查和审计
        log.info("保存结果日志成功{}", rlog);
    }
}
