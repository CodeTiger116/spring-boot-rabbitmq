package com.hanhu.springbootrabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 优先级队列
 *
 */
@Configuration
public class PriorityQueueConfig {

    public static final String EXCHANGE_NAME = "PriX";
    public static final String QUEUE_NAME = "PriQ";
    public static final String ROUTING_KEY = "XQ";



    @Bean("exchange")
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }


    @Bean("queue")
    public Queue queue() {
        Map<String, Object> args = new HashMap<>();
        // 最大优先级为 10
        args.put("x-max-priority", 10);
        return QueueBuilder.durable(QUEUE_NAME).withArguments(args).build();
    }

    @Bean
    public Binding queueBindingExchange(@Qualifier("queue") Queue queue, @Qualifier("exchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

}

