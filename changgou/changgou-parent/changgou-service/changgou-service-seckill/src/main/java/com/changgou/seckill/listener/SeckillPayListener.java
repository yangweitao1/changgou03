package com.changgou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.dao.SeckillOrderMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.SystemConstants;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 监听秒杀的队列
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.seckill.listener *
 * @since 1.0
 */
@Component
@RabbitListener(queues = "queue.seckillorder")//监听指定的队列
public class SeckillPayListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;


    @RabbitHandler//处理消息
    public void handlerMsg(String msg) throws Exception {
        //1.获取消息
        //2.转成MAP
        Map<String, String> resultMap = JSON.parseObject(msg, Map.class);
        if (resultMap != null) {
            //通信成功  不代表业务成功
            if (resultMap.get("return_code").equalsIgnoreCase("SUCCESS")) {
                String attach = resultMap.get("attach");// 有 exchange 有 routingkey 有username
                Map map = JSON.parseObject(attach, Map.class);
                //3.判断是否支付成功  如果成功
                if (resultMap.get("result_code").equalsIgnoreCase("SUCCESS")) {
                    /**
                     *
                     1.更新预订单的状态,更新到mysql
                     2.删除排队标识(抢单信息)
                     3.删除重复下单(统计排队数量的)
                     4.删除预订单
                     */

                    //获取redis中某一个用户的订单
                    SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).get(map.get("username"));
                    if(seckillOrder!=null) {
                        String time_end = resultMap.get("time_end");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date time_end1 = dateFormat.parse(time_end);
                        seckillOrder.setPayTime(time_end1);//支付时间
                        seckillOrder.setTransactionId(resultMap.get("transaction_id"));//交易流水号
                        seckillOrder.setStatus("1");//已经支付
                        seckillOrderMapper.insertSelective(seckillOrder);//存储到数据库中

                        //删除预订单
                        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(map.get("username"));

                        //删除防止重复排队下单的信息
                        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(map.get("username"));

                        //删除排队标识
                        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(map.get("username"));
                    }

                } else {
                    //4.判断是否支付成功  如果失败

                    /**
                     *
                     *
                     *
                     * 1.删除预订单
                     2.恢复库存
                     3.清理排队标识(抢单的状态)
                     4.清理统计排队标识(防止重复下单)
                     */
                    SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).get(map.get("username"));
                    // 先获取商品的数据
                    SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).get(seckillStatus.getGoodsId());

                    if(seckillGoods==null){
                        //从数据库重获取该商品的数据
                        seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillStatus.getGoodsId());
                    }

                    //1.恢复原来的商品的库存+1
                    seckillGoods.setStockCount(seckillGoods.getStockCount()+1);

                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + seckillStatus.getTime()).put(seckillStatus.getGoodsId(),seckillGoods);

                    //2.将队列的数据重新推送一个元素
                    redisTemplate.boundListOps(SystemConstants.SEC_KILL_CHAOMAI_LIST_KEY_PREFIX + seckillStatus.getGoodsId()).leftPush(seckillStatus.getGoodsId());


                    //删除防止重复排队下单的信息
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(map.get("username"));

                    //删除排队标识
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(map.get("username"));

                    //删除预订单
                    redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).delete(map.get("username"));
                }
            }
        }


    }



    /*public void clearQueue(SeckillStatus seckillStatus){
        //防止重复排队标识
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(seckillStatus.getUsername());

        //清理抢单标示
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(seckillStatus.getUsername());
    }*/
}
