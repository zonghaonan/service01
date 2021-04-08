package com.xuecheng.test.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author zhn
 * @Date 2021/3/24 13:52
 * @Version 1.0
 */
@Configuration
public class RabbitmqConfig {
    public static  final String  QUEUE_INFORM_EMAIL  =  "queue_inform_email";
    public  static  final String  QUEUE_INFORM_SMS  =  "queue_inform_sms";
    public  static  final String  EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    public static final String ROUTINGKEY_EMAIL = "inform.#.email.#";
    public static final String ROUTINGKEY_SMS = "inform.#.sms.#";
    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange EXCHANGE_TOPICS_INFORM(){
        //durable(true) 持久化，mq重启之后交换机还在
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }
    //声明队列
    @Bean(QUEUE_INFORM_EMAIL)
    public Queue QUEUE_INFORM_EMAIL(){
        return new Queue(QUEUE_INFORM_EMAIL);
    }
    @Bean(QUEUE_INFORM_SMS)
    public Queue QUEUE_INFORM_SMS(){
        return new Queue(QUEUE_INFORM_SMS);
    }
    //QUEUE_INFORM_EMAIL队列绑定交换机
    @Bean
    public Binding BINDING_QUEUE_INFORM_EMAIL(@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange,
                                              @Qualifier(QUEUE_INFORM_EMAIL) Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_EMAIL).noargs();
    }
    //QUEUE_INFORM_SMS队列绑定交换机
    @Bean
    public Binding BINDING_QUEUE_INFORM_SMS(@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange,
                                              @Qualifier(QUEUE_INFORM_SMS) Queue queue){
        return BindingBuilder.bind(queue).to(exchange).with(ROUTINGKEY_SMS).noargs();
    }
}
