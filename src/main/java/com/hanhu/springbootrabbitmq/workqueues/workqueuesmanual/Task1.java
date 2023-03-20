package com.hanhu.springbootrabbitmq.workqueues.workqueuesmanual;

import com.hanhu.springbootrabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

/**
 * 消息手动应答时，是不丢失、放回队列中重新消费
 */
public class Task1 {
    //队列名称
    private static final String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] argv) throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            //队列持久化
            channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
            //从控制台中输入信息
            Scanner sc = new Scanner(System.in);
            System.out.println("请输入信息");
            while (sc.hasNext()) {
                String message = sc.nextLine();
                //消息持久化
                channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
                System.out.println("生产者发出消息" + message);
            }
        }

    }
}
