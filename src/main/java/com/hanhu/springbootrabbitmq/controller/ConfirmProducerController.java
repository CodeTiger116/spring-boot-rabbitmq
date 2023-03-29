package com.hanhu.springbootrabbitmq.controller;

import com.hanhu.springbootrabbitmq.config.ConfirmConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 发布确认
 */
@RestController
@RequestMapping("/confirm")
@Slf4j
public class ConfirmProducerController {
    public static final String CONFIRM_EXCHANGE_NAME = "confirm.exchange";
    @Autowired
    private RabbitTemplate rabbitTemplate;

    //@Autowired
    //private MyCallBack myCallBack;

    //依赖注入 rabbitTemplate 之后再设置它的回调对象
    //@PostConstruct
    //public void init() {
    //    rabbitTemplate.setConfirmCallback(myCallBack);
    //}

    @GetMapping("sendMessage/{message}")
    public void sendMessage(@PathVariable String message) throws InterruptedException {
        // 交换机和路由键都正确
        CorrelationData correlationData1 = new CorrelationData("1");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE, ConfirmConfig.CONFIRM_ROUTING_KEY, message, correlationData1);
        log.info("1-发送的消息内容为：{}", message);
        TimeUnit.SECONDS.sleep(1);

        // 交换机不正确，路由键正确
        CorrelationData correlationData2 = new CorrelationData("2");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE + "ddd", ConfirmConfig.CONFIRM_ROUTING_KEY, message, correlationData2);
        log.info("2-发送的消息内容为：{}", message);
        TimeUnit.SECONDS.sleep(1);

        // 交换机正确，路由键不正确（找不到队列）
        CorrelationData correlationData3 = new CorrelationData("3");
        rabbitTemplate.convertAndSend(ConfirmConfig.CONFIRM_EXCHANGE, ConfirmConfig.CONFIRM_ROUTING_KEY + "ddd", message, correlationData3);
        log.info("3-发送的消息内容为：{}", message);
    }

}
