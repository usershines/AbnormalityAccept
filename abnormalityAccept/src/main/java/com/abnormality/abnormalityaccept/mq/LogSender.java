package com.abnormality.abnormalityaccept.mq;

import cn.hutool.json.JSONUtil;
import com.abnormality.abnormalityaccept.entity.log.ExceptionLog;
import com.abnormality.abnormalityaccept.entity.log.ResultLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 日志发送器，用于将日志信息发送至 RabbitMQ 消息队列。
 *
 * <p>该类为 Spring 组件，封装了 RabbitMQ 的消息发送逻辑，支持发送原始字符串消息
 * 以及将 ExceptionLog 实体对象转换为 JSON 格式后发送至指定交换机和路由键。</p>
 * <p>
 *     <strong>
 *         用于异步写入日志以减轻服务器压力，同时实现日志功能与具体服务器的解耦，方便多机部署。
 *     </strong>
 * </p>
 */
@Component
@Slf4j
public class LogSender {

    /**
     * RabbitMQ 模板类，用于将消息发送至 RabbitMQ。
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 直接交换机实例，用于指定消息发送的目标交换机。
     */
    @Autowired
    private DirectExchange directExchange;

    @Value("${log.save-exception-log}")
    private boolean saveExceptionLog;

    @Value("${log.save-result-log}")
    private boolean saveResultLog;

    /**
     * 发送原始字符串消息至 RabbitMQ。
     *
     * <p>该方法将消息发送至当前 DirectExchange，并使用固定的路由键 "routing.key"。</p>
     *
     * @param message 要发送的原始字符串消息
     */
    public void send(String message) {
        // 使用 RabbitTemplate 发送消息，指定交换机名称和路由键
        rabbitTemplate.convertAndSend(directExchange.getName(), "routing.key", message);
    }

    /**
     * 将异常日志对象转换为 JSON 字符串，并发送至 RabbitMQ。
     *
     * <p>该方法的主要流程如下：</p>
     * <ul>
     *   <li>将传入的 ExceptionLog 对象序列化为 JSON 格式的字符串。</li>
     *   <li>通过 RabbitTemplate 发送消息至 RabbitMQ，使用固定的路由键 "log"。</li>
     *   <li>记录日志信息，用于调试或监控消息发送情况。</li>
     * </ul>
     *
     * @param elog 包含异常日志信息的 ExceptionLog 实体对象
     */
    public void saveLog(ExceptionLog elog) {

        if(!saveExceptionLog){
            return;
        }
        // 将 ExceptionLog 对象转换为 JSON 字符串格式
        String message = JSONUtil.toJsonStr(elog);

        // 发送消息至 RabbitMQ，使用指定交换机和路由键
        rabbitTemplate.convertAndSend(directExchange.getName(), "log.exception", message);

        // 记录已发送的日志内容，便于调试和监控
        log.info("发送异常日志到消息队列：{}", message);
    }

    public void savelog(ResultLog rlog){
        if(!saveResultLog){
            return;
        }
        String message = JSONUtil.toJsonStr(rlog);
        rabbitTemplate.convertAndSend(directExchange.getName(), "log.result", message);
        log.info("发送结果日志到消息队列：{}", message);

    }
}

