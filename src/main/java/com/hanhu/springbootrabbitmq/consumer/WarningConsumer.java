package com.hanhu.springbootrabbitmq.consumer;

import com.hanhu.springbootrabbitmq.config.ConfirmConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 报警消费者
 */
@Slf4j
@Component
public class WarningConsumer {
    @RabbitListener(queues = ConfirmConfig.WARNING_QUEUE)
    public void receiveQueueDead(Message message, Channel channel) {
        String msg = new String(message.getBody());
        log.info("warning consumer 收到不可路由的消息为：{}", msg);
    }
}
