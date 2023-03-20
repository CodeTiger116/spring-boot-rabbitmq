package com.hanhu.springbootrabbitmq.publishconfirm;

import com.hanhu.springbootrabbitmq.utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.logging.Logger;

/**
 * 发布确认模式
 * 1、单个确认
 * 2、批量确认
 * 3、异步确认
 *
 * @author hanhu
 */

public class PublishConfirm {
    private static final int MESSAGE_COUNT = 1000;

    public static void main(String[] args) throws Exception {
        //单个确认 7268ms
        //PublishConfirm.publishMessageSingleConfirm();
        //批量确认 179ms
        //PublishConfirm.publishMessageBatchConfirm();
        //异步确认 40ms
        //PublishConfirm.publishMessageAsyncConfirm();

    }


    //单个确认
    public static void publishMessageSingleConfirm() throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();
            long begin = System.currentTimeMillis();
            //批量发消息
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = i + "";
                channel.basicPublish("", queueName, null, message.getBytes());
                //服务端返回 false 或超时时间内未返回，生产者可以消息重发
                boolean flag = channel.waitForConfirms();
                if (flag) {
                    System.out.println("消息发送成功");

                }
            }
            long end = System.currentTimeMillis();
            //耗时7268ms
            System.out.println("发布" + MESSAGE_COUNT + "个单独确认消息,耗时" + (end - begin) + "ms");
        }
    }

    //批量确认
    public static void publishMessageBatchConfirm() throws Exception {
        try (Channel channel = RabbitMqUtils.getChannel()) {
            String queueName = UUID.randomUUID().toString();
            channel.queueDeclare(queueName, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();
            //批量确认消息大小
            int batchSize = 100;
            //每发布100个消息，确认一次
            int batchConfirmSize = 0;
            long begin = System.currentTimeMillis();
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = i + "";
                channel.basicPublish("", queueName, null, message.getBytes());
                batchConfirmSize++;
                if (batchConfirmSize % batchSize == 0) {
                    //确认
                    boolean flag = channel.waitForConfirms();
                    if (flag) {
                        System.out.println("消息发送成功");
                    }
                }
            }
            //为了确保还有剩余没有确认消息 再次确认
            if (batchConfirmSize > 0) {
                channel.waitForConfirms();
            }
            long end = System.currentTimeMillis();
            //179ms
            System.out.println("发布" + MESSAGE_COUNT + "个批量确认消息,耗时" + (end - begin) + "ms");
        }
    }


    //异步确认
    public static void publishMessageAsyncConfirm() throws Exception {
        String queueName = UUID.randomUUID().toString();
        try (Channel channel = RabbitMqUtils.getChannel();) {
            //创建一个队列
            channel.queueDeclare(queueName, false, false, false, null);
            //开启发布确认
            channel.confirmSelect();
            /**
             * 线程安全有序的一个哈希表，适用于高并发的情况
             * 1、轻松的将序号和消息进行关联
             * 2、轻松的批量删除条目  只需要给到序列号
             * 3、支持并发访问
             * */
            ConcurrentSkipListMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
            /**
             * 确认收到消息的一个回调
             * 1、参数1：当前收到的消息的序列号
             * 2、参数2：是否批量确认
             */
            ConfirmCallback ackCallBack = (sequenceNumber, multiple) -> {
                if (multiple) {
                    System.out.println("生产者发布的消息" + outstandingConfirms.get(sequenceNumber) + "被确认，序列号" + sequenceNumber);
                    //返回的是小于等于当前序列号的未确认消息，是一个map
                    ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(sequenceNumber, true);
                    //消除该部分未确认消息
                    confirmed.clear();
                } else {
                    System.out.println("生产者发布的消息" + outstandingConfirms.get(sequenceNumber) + "被确认，序列号" + sequenceNumber);
                    //只消除当前序号的消息
                    outstandingConfirms.remove(sequenceNumber);
                }

            };
            /**
             * 消息未确认的一个回调
             * 1、参数1：当前收到的消息的序列号
             * 2、参数2：是否批量确认
             */
            ConfirmCallback nackCallBack = (sequenceNumber, multiple) -> {
                String message = outstandingConfirms.get(sequenceNumber);
                System.out.println("生产者发布的消息" + message + "未被确认，序列号" + sequenceNumber);
            };
            /**
              添加一个异步确认的监听器
                1、确认收到消息的回调
                2、未收到消息的回调
             */
            channel.addConfirmListener(ackCallBack, nackCallBack);

            long begin = System.currentTimeMillis();
            //批量发送消息
            for (int i = 0; i < MESSAGE_COUNT; i++) {
                String message = i + "";
                outstandingConfirms.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish("", queueName, null, message.getBytes());
            }
            long end = System.currentTimeMillis();
            //40ms
            System.out.println("发布" + MESSAGE_COUNT + "个异步确认消息，耗时" + (end - begin) + "ms");
        }
    }

}
