package com.xuecheng.test.rabbitmq.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Author zhn
 * @Date 2021/3/24 14:18
 * @Version 1.0
 */
@Component
public class ReceiveHandler {
    @RabbitListener(queues={RabbitmqConfig.QUEUE_INFORM_EMAIL})
    public void get_email(String msg, Message message, Channel channel){
        System.out.println(msg);
    }
}
