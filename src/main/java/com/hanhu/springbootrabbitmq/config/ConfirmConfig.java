package com.hanhu.springbootrabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 发布确认高级
 * 1、消息确认
 * 2、消息回退
 * 3、交换机备份
 */

@Configuration
public class ConfirmConfig {
    //确认交换机
    public static final String CONFIRM_EXCHANGE = "confirm.exchange";
    //确认队列
    public static final String CONFIRM_QUEUE = "confirm.queue";
    //确认交换机和确认队列的routingkey
    //public static final String CONFIRM_ROUTING_KEY = "confirm.routing.key";

    //备份交换机
    public static final String BACKUP_EXCHANGE = "backup.exchange";
    //备份队列
    public static final String BACKUP_QUEUE = "backup.queue";
    //报警队列
    public static final String WARNING_QUEUE = "warning.queue";
    public static final String CONFIRM_ROUTING_KEY = "key1";


    //声明确认 Exchange
    /*@Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        return new DirectExchange(CONFIRM_EXCHANGE);
    }*/

    // 声明确认 Exchange 交换机的备份交换机
    @Bean("confirmExchange")
    public DirectExchange confirmExchange() {
        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE)
                .durable(true)
                // 设置该交换机的备份交换机
                .withArgument("alternate-exchange", BACKUP_EXCHANGE)
                .build();
    }

    // 声明确认队列
    @Bean("confirmQueue")
    public Queue confirmQueue() {
        return QueueBuilder.durable(CONFIRM_QUEUE).build();
    }

    // 声明确认队列绑定关系
    @Bean
    public Binding queueBinding(@Qualifier("confirmQueue") Queue queue,
                                @Qualifier("confirmExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(CONFIRM_ROUTING_KEY);
    }


    // 声明备份 Exchange
    @Bean("backupExchange")
    public FanoutExchange backupExchange() {
        return new FanoutExchange(BACKUP_EXCHANGE);
    }

    // 声明备份队列
    @Bean("backupQueue")
    public Queue backupQueue() {
        return QueueBuilder.durable(BACKUP_QUEUE).build();
    }

    // 声明警告队列
    @Bean("warningQueue")
    public Queue warningQueue() {
        return QueueBuilder.durable(WARNING_QUEUE).build();
    }

    // 声明备份队列绑定备份交换机关系
    @Bean
    public Binding backupQueueBindExchange(
            @Qualifier("backupQueue") Queue backupQueue,
            @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(backupQueue).to(backupExchange);
    }

    // 声明报警队列绑定备份交换机关系
    @Bean
    public Binding warningQueueBindExchange(
            @Qualifier("warningQueue") Queue warningQueue,
            @Qualifier("backupExchange") FanoutExchange backupExchange) {
        return BindingBuilder.bind(warningQueue).to(backupExchange);
    }


}
