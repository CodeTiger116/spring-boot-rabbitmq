package com.hanhu.springbootrabbitmq.deadletterqueue;

import com.hanhu.springbootrabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

/**
 * 1、模拟消息ttl过期
 * 2、模拟队列达到最大长度
 * 3、模拟消息被拒
 *
 * @author hanhu
 */
public class Producer {
    private static final String NORMAL_EXCHANGE = "normal_exchange";
    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
            //设置消息的 TTL 时间 10s
            //AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().expiration("10000").build();


            //该信息是用作演示队列个数限制
            for (int i = 1; i < 11 ; i++) {
                String message="info"+i;
                //channel.basicPublish(NORMAL_EXCHANGE,"zhangsan", properties, message.getBytes());
                channel.basicPublish(NORMAL_EXCHANGE,"zhangsan", null, message.getBytes());
                System.out.println("生产者发送消息:"+message);
            }
        }
    }
}
