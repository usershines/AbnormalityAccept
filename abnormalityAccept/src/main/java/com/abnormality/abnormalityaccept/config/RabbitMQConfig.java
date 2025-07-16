package com.abnormality.abnormalityaccept.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 配置类用于定义 RabbitMQ 的相关组件，包括交换机、队列以及它们之间的绑定关系。
 * 该配置类通过声明 Bean 的方式，使得 Spring 容器可以管理 RabbitMQ 的基础组件。
 * </p>
 */
@Configuration
public class RabbitMQConfig {

    /**
     * <p>
     * 创建一个 DirectExchange 类型的交换机 Bean。
     * DirectExchange 是一种直连交换机，消息会根据路由键精确匹配发送到对应的队列。
     * </p>
     *
     * @return 返回一个 DirectExchange 实例，表示名为 "myDirectExchange" 的交换机。
     */
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("myDirectExchange");
    }

    /**
     * <p>
     * 创建一个 Queue 类型的队列 Bean。
     * 该队列用于接收与日志相关的消息，队列名称为 "logQueue"。
     * </p>
     *
     * @return 返回一个 Queue 实例，表示名为 "logQueue" 的队列。
     */
    @Bean
    public Queue logQueue() {
        return new Queue("logQueue");
    }

    /**
     * <p>
     * 创建一个 Binding 类型的绑定 Bean，用于将队列绑定到指定的交换机上。
     * 通过路由键 "log" 将队列和交换机关联，使得发送到该交换机且路由键为 "log" 的消息
     * 能够被正确地路由到绑定的队列中。
     * </p>
     *
     * @param directExchange 注入的 DirectExchange 实例，代表名为 "myDirectExchange" 的交换机。
     * @param myQueue        注入的 Queue 实例，代表名为 "logQueue" 的队列。
     * @return 返回一个 Binding 实例，表示队列和交换机之间的绑定关系。
     */
    @Bean
    public Binding binding(DirectExchange directExchange, Queue myQueue) {
        return BindingBuilder.bind(myQueue).to(directExchange).with("log");
    }
}
