package com.changgou.pay.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.pay.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/weixin/pay")
public class WeixinPayController {

    @Autowired
    private WeixinPayService weixinPayService;

    /**
     * 生成二维码
     *
     * @param paramters    页面传递过来的数据:包括订单号,和金额 和 附加数据
     * @return
     */
    @RequestMapping("/create/native")
//    public Result<Map> createNative(String out_trade_no, String total_fee) {
    public Result<Map> createNative(@RequestParam Map<String, String> paramters) {
        Map<String, String> resultMap = weixinPayService.createNative(paramters);
        return new Result<Map>(true, StatusCode.OK, "创建二维码成功", resultMap);
    }

    /**
     * 根据支付的订单号 查询该订单的支付的状态
     *
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/status/query")
    public Result<Map> queryStatus(String out_trade_no) {
        Map<String, String> resultMap = weixinPayService.queryStatus(out_trade_no);
        return new Result<Map>(true, StatusCode.OK, "查询状态成功", resultMap);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${mq.pay.exchange.order}")
    private String exchange;
    @Value("${mq.pay.queue.order}")
    private String queue;
    @Value("${mq.pay.routing.key}")
    private String routing;

    /**
     * 作用就是:接收微信传递过来的数据流信息
     */
    @RequestMapping("/notify/url")
    public String notifyUrlhandler(HttpServletRequest request) {

        try {
            //接收微信支付系统发送的请求里面的数据流信息
            ServletInputStream inputStream = request.getInputStream();


            //创建字节输出流
            ByteArrayOutputStream bous = new ByteArrayOutputStream();
            //将数据写入到输出流中
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                bous.write(buffer, 0, len);
            }
            //转成字节数据
            byte[] bytes = bous.toByteArray();
            //字节数组转成字符串XML
            String map = new String(bytes, "utf-8");

            System.out.println(map);
            //转成 map
            Map<String, String> mapObject = WXPayUtil.xmlToMap(map);  //有附加数据
            //根据需要使用里面的数据
            String attach = mapObject.get("attach");//json格式的字符串 本身是有username

            Map<String, String> mapAttach = JSON.parseObject(attach, Map.class);//本身是有用户名信息
            //需要设置用户名
            //mapAttach.put("username","szitheima");

            //返回给微信支付系统响应(表示接收到了通知 支付成功)  xml字符串

            Map<String, String> resultMap = new HashMap<>();
            resultMap.put("return_code", "SUCCESS");
            resultMap.put("return_msg", "OK");
            String s = WXPayUtil.mapToXml(resultMap);

            //发送消息给rabbitmq  接收附加参数的值: 你要提前区分普通还是秒杀
            //根据传递过来的附加参数获取到 要发送的EXCHANGE 和routingkey
//            rabbitTemplate.convertAndSend(exchange, routing, JSON.toJSONString(mapObject));
            rabbitTemplate.convertAndSend(mapAttach.get("exchange"), mapAttach.get("routingkey"), JSON.toJSONString(mapObject));


            return s;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
