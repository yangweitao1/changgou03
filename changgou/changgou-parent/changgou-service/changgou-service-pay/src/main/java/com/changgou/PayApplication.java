package com.changgou;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou *
 * @since 1.0
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//排除
@EnableEurekaClient
public class PayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class,args);
    }

    @Autowired
    private Environment environment;


    //创建队列
    @Bean
    public Queue createQueue(){
        String propertyqueuename = environment.getProperty("mq.pay.queue.order");
        Queue queue = new Queue(propertyqueuename);
        return queue;
    }

    //创建交换机
    @Bean
    public DirectExchange createExchange(){
        return new DirectExchange(environment.getProperty("mq.pay.exchange.order"));
    }

    //绑定  -->routingkey

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(createQueue()).to(createExchange()).with(environment.getProperty("mq.pay.routing.key"));
    }




    //创建队列
    @Bean
    public Queue createQueueSeckill(){
        String propertyqueuename = environment.getProperty("mq.pay.queue.seckillorder");
        Queue queue = new Queue(propertyqueuename);
        return queue;
    }

    //创建交换机
    @Bean
    public DirectExchange createExchangeSeckill(){
        return new DirectExchange(environment.getProperty("mq.pay.exchange.seckillorder"));
    }

    //绑定  -->routingkey

    @Bean
    public Binding bindingSeckill(){
        return BindingBuilder.bind(createQueue()).to(createExchange()).with(environment.getProperty("mq.pay.routing.seckillkey"));
    }


}
