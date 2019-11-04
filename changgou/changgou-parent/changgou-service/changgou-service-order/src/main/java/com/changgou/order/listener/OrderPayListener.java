package com.changgou.order.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.order.listener *
 * @since 1.0
 */
@Component
@RabbitListener(queues = "queue.order")//指定监听的队列
public class OrderPayListener {


    @Autowired
    private OrderService orderService;

    //处理消息
    @RabbitHandler
    public void handlerMsg(String msg) {
        System.out.println("订单微服务的数据:"+msg);

        //1.接收消息 JSON字符串

        //2.转成Map对象
        Map<String, String> resultMap = JSON.parseObject(msg, Map.class);

        if (resultMap != null) {

            if (resultMap.get("return_code").equalsIgnoreCase("SUCCESS")) {
                //3.获取里面的订单号 和支付时间 等信息
                if (resultMap.get("result_code").equalsIgnoreCase("SUCCESS")) {
                    //4.判断微信的状态值,支付成功 来更新订单的状态

                    //更新订单的付款时间,更新交易流水,更新 支付的状态
                    orderService.updateStatus(resultMap.get("transaction_id"),resultMap.get("out_trade_no"),resultMap.get("time_end"));

                } else {
                    //5.判断微信的状态值,支付失败 1 关闭微信订单 1.删除订单 2. 恢复库存 3.减少积分
                    System.out.println("删除订单");
                }
            }

        } else {
            //有问题 支付失败
        }

    }


}
