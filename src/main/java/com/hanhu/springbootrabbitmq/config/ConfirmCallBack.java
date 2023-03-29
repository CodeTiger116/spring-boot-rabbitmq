package com.hanhu.springbootrabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * 发布确认高级
 * 1、消息确认
 * 2、消息会退
 */

@Slf4j
@Component  // 第一步注入
public class ConfirmCallBack implements RabbitTemplate.ConfirmCallback ,RabbitTemplate.ReturnsCallback{

    @Resource   // 第二步注入
    private RabbitTemplate rabbitTemplate;

    // @PostConstruct 注解,在对象加载完依赖注入后执行它通常都是一些初始化的操作，
    // 但初始化可能依赖于注入的其他组件，所以要等依赖全部加载完再执行
    // 第三步注入
    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    /**
     * @param correlationData 保存回调消息的 ID 及相关信息
     * @param ack             交换机是否收到消息
     * @param cause           失败的原因
     * @desc 交换机确认回调方法， 发消息 交换机接收到消息触发回调函数
     * @auth hanhu
     * @date 2023/3/28 22:40
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            log.info("交换机已收到ID为：{} 的消息, cause:{}", correlationData.getId(), cause);
        } else {
            log.info("交换机未收到ID为：{} 的消息, cause:{}", correlationData.getId(), cause);
        }
    }


    // 只有消息不可路由才会调用此函数，成功不调用
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.error("消息 {}, 被交换机 {} 退回, 应答代码 {}, 原因 {}, 路由 {}",
                new String(returnedMessage.getMessage().getBody()),
                returnedMessage.getExchange(),
                returnedMessage.getReplyCode(),
                returnedMessage.getReplyText(),
                returnedMessage.getRoutingKey());
    }
}
