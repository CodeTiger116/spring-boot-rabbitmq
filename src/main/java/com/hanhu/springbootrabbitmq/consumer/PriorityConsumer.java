package com.hanhu.springbootrabbitmq.consumer;

import com.hanhu.springbootrabbitmq.config.PriorityQueueConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;


/**
 * 优先级队列
 * -消费者
 * -先不启动消费者，将代码注释，生产者发送完消息，再取消注释重启项目
 */
@Slf4j
@Component
public class PriorityConsumer {

    @RabbitListener(queues = PriorityQueueConfig.QUEUE_NAME)
    public void receiveD(Message message) {
        String msg = new String(message.getBody());
        log.info("当前时间：{}，收到的消息{}", new Date().toString(), msg);

    }


}

