package com.hanhu.springbootrabbitmq.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RequestMapping("/ttl")
@RestController
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public static final String DELAYED_EXCHANGE_NAME = "delayed.exchange";
    public static final String DELAYED_ROUTING_KEY = "delayed.routingkey";


    /**
     * 延迟队列
     * @param message
     */
    @GetMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable String message) {
        log.info("当前时间:{},发送一条信息给两个 TTL 队列:{}", new Date(), message);
        rabbitTemplate.convertAndSend("X", "XA", "消息来自 ttl 为 10S 的队列: " + message);
        rabbitTemplate.convertAndSend("X", "XB", "消息来自 ttl 为 40S 的队列: " + message);
    }

    /**
     * 延迟队列优化
     * @param message
     * @param ttlTime
     */
    @GetMapping("/sendExpirationMsg/{message}/{ttlTime}")
    public void sendExpirationMsg(@PathVariable String message, @PathVariable String ttlTime) {
        rabbitTemplate.convertAndSend("X", "XC", message, correlationData -> {
            correlationData.getMessageProperties().setExpiration(ttlTime);
            return correlationData;
        });
        log.info("当前时间:{},发送一条时长{}毫秒 TTL 信息给队列 C:{}", new Date(), ttlTime, message);
    }

    /**
     * 基于插件实现延迟队列
     * @param message
     * @param delayedTime
     */
    @GetMapping("sendDelayedMsg/{message}/{delayedTime}")
    public void senDelayedMsg(@PathVariable String message, @PathVariable Integer delayedTime) {
        rabbitTemplate.convertAndSend(DELAYED_EXCHANGE_NAME, DELAYED_ROUTING_KEY, message, correlationData -> {
            correlationData.getMessageProperties().setDelay(delayedTime);
            return correlationData;
        });
        log.info(" 当前时间:{}, 发送一条延迟{} 毫秒的信息给队列 delayed.queue:{}", new Date(), delayedTime, message);
    }
}

