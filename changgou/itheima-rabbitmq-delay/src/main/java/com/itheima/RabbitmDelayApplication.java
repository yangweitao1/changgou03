package com.itheima;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.itheima *
 * @since 1.0
 */
@SpringBootApplication
@EnableRabbit
public class RabbitmDelayApplication {

    public static void main(String[] args) {
        SpringApplication.run(RabbitmDelayApplication.class, args);
    }


    //+ 创建队列1 (用于发送消息到的队列)

    //  queue1  过期--->dlx.exchange(队列2要绑定到该交换机)--->queue.message-->给队列2(routringKey必须和这个参数中设置保持一致)
    //队列1
    @Bean
    public Queue createQueue1(){
        return QueueBuilder.durable("queue.message.delay")//设置一个队列名
        .withArgument("x-dead-letter-exchange", "dlx.exchange")//设置死信交换机
                .withArgument("x-dead-letter-routing-key", "queue.message").build();//设置路由key
    }

    // 创建队列2 (用于接收消息的队列)

    @Bean
    public Queue createQueue2(){
        return new Queue("queue2");
    }

    //+ 创建交换机(dlx.exchange)
    @Bean
    public DirectExchange createExchange(){
        return  new DirectExchange("dlx.exchange");
    }


    //+ 绑定交换机到队列2中
    @Bean
    public Binding createBinding(){
        return BindingBuilder.bind(createQueue2()).to(createExchange()).with("queue.message");
    }
}
