package com.hanhu.springbootrabbitmq.controller;

import com.hanhu.springbootrabbitmq.config.PriorityQueueConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 优先级队列
 * -消息生产者
 */
@Slf4j
@RestController
@RequestMapping("/priority")
public class PriorityProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostMapping(value = "/sendPriorityMessage")
    public void sendPriorityMessage() {
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;

            if (i == 10) {
                rabbitTemplate.convertAndSend(PriorityQueueConfig.EXCHANGE_NAME, PriorityQueueConfig.ROUTING_KEY, message, correlationData -> {

                    correlationData.getMessageProperties().setPriority(5);
                    return correlationData;
                });
            } else {
                rabbitTemplate.convertAndSend(PriorityQueueConfig.EXCHANGE_NAME, PriorityQueueConfig.ROUTING_KEY, message);
            }


        }
    }

}

