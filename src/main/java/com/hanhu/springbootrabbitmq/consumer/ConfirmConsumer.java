package com.hanhu.springbootrabbitmq.consumer;

import com.hanhu.springbootrabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 发布确认高级 -- 消费者
 */
@Slf4j
@Component
public class ConfirmConsumer {

    @RabbitListener(queues = ConfirmConfig.CONFIRM_QUEUE)
    public void receiveMsg(Message message) {
        String msg = new String(message.getBody());
        log.info("当前时间:{},接受到队列 confirm.queue 消息:{}", new Date(),msg);
    }
}
