package com.hanhu.springbootrabbitmq.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 生产者
 *
 * @author hh
 */
public class Producer {
    private static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("43.143.148.254");
        factory.setUsername("admin");
        factory.setPassword("123");
        //创建连接，获取信道
        //channel 实现了自动 close 接口 自动关闭 不需要显示关闭
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()){
            /**
             * 生成一个队列
             * 1.队列名称
             * 2.队列里面的消息是否持久化 默认消息存储在内存中
             * 3.是否排外的，true：队列仅对首次声明它的连接可见，是该Connection私有的，false：不同连接的管道也可以使用该队列
             * 4.是否自动删除 最后一个消费者端开连接以后 该队列是否自动删除 true 自动删除
             * 5.其他参数
             */
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "hello world";
            /**
             * 发送一个消息
             * 1.发送到那个交换机
             * 2.路由的 key 是哪个
             * 3.其他的参数信息
             * 4.发送消息的消息体
             */
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
            System.out.println("消息发送完毕");
        }
    }

}
