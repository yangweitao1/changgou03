package com.changgou.pay.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.pay.service.WeixinPayService;
import com.github.wxpay.sdk.WXPayUtil;
import entity.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.pay.service.impl *
 * @since 1.0
 */
@Service
public class WeixinPayServiceImpl implements WeixinPayService {


    @Value("${weixin.notifyurl}")
    private String notifyurl;

    /**
     * 给微信支付系统发送请求:调用统一下单的API  获取响应(code_url)
     *
     * @param out_trade_no
     * @param total_fee
     * @return
     */
    @Override
    public Map<String, String> createNative(Map<String,String> parameters) {

        try {
            //1.组装参数 使用map来组装 再转成XML

            Map<String, String> paramMap = new HashMap<>();

            paramMap.put("appid", "wx8397f8696b538317");
            paramMap.put("mch_id", "1473426802");//商户号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//
            paramMap.put("body", "畅购");//
            paramMap.put("out_trade_no", parameters.get("out_trade_no"));
            paramMap.put("total_fee", parameters.get("total_fee"));//单位是分
            paramMap.put("spbill_create_ip", "127.0.0.1");//
            paramMap.put("notify_url", notifyurl);//异步通知接收的回调地址
            paramMap.put("trade_type", "NATIVE");//支付类型



            //添加附加数据 长度为127 最多
            paramMap.put("attach", JSON.toJSONString(parameters));//支付类型  paramster里面有:1.exchange,2 routingkey.3.username

            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");//秘钥

            //2.使用Httpclient 模拟浏览器发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);//请求体
            httpClient.post();
            //3.使用httpclient 模拟浏览器接收响应(xml)

            String content = httpClient.getContent();
            System.out.println(content);
            //4.将XML转成Map
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            //5.返回

            Map<String,String> resultMap = new HashMap<>();
            resultMap.put("code_url",map.get("code_url"));
            resultMap.put("out_trade_no", parameters.get("out_trade_no"));
            resultMap.put("total_fee",parameters.get("total_fee"));
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, String> queryStatus(String out_trade_no) {
        try {
            //1.组装参数 使用map来组装 再转成XML

            Map<String, String> paramMap = new HashMap<>();

            paramMap.put("appid", "wx8397f8696b538317");
            paramMap.put("mch_id", "1473426802");//商户号
            paramMap.put("nonce_str", WXPayUtil.generateNonceStr());//
            paramMap.put("out_trade_no", out_trade_no);

            //自动添加签名
            String xmlParam = WXPayUtil.generateSignedXml(paramMap, "T6m9iK73b0kn9g5v426MKfHQH7X8rKwb");//秘钥

            //2.使用Httpclient 模拟浏览器发送请求
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);//请求体
            httpClient.post();
            //3.使用httpclient 模拟浏览器接收响应(xml)

            String content = httpClient.getContent();
            System.out.println(content);
            //4.将XML转成Map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(content);
            //5.返回
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
