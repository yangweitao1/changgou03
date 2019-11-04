package com.itheima.listener;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.itheima.listener *
 * @since 1.0
 */
@Component
@RabbitListener(queues = "queue2")
public class RabbitmqListner {

    @RabbitHandler
    public void consumer(String messsage){
        System.out.println("date:"+new Date());
        System.out.println("消息收到了:"+messsage);
    }
}
