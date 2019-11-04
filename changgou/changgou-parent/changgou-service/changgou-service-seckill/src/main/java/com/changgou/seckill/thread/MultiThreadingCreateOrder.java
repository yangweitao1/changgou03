package com.changgou.seckill.thread;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import com.changgou.seckill.pojo.SeckillOrder;
import com.changgou.seckill.pojo.SeckillStatus;
import entity.IdWorker;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.seckill.thread *
 * @since 1.0
 */
@Component
public class MultiThreadingCreateOrder {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Async//异步调用 多线程
    public void handlerOrder(){

      /*  try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        //从队列中获取状态POJO

        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SystemConstants.SEC_KILL_USER_QUEUE_KEY).rightPop();

        if(seckillStatus!=null){
            String time= seckillStatus.getTime();
            String username=seckillStatus.getUsername();
            Long id = seckillStatus.getGoodsId();

            //从队列中获取元素 弹出,如果为空 说明卖完了
            Object o = redisTemplate.boundListOps(SystemConstants.SEC_KILL_CHAOMAI_LIST_KEY_PREFIX + id).rightPop();
            if(o==null){
                clearQueue(seckillStatus);
                throw new RuntimeException("redis超卖问题解决了卖完了");
            }


            //1.根据时间段 和 商品的id获取商品的数据

            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).get(id);





            System.out.println("5秒前库存为:"+seckillGoods.getStockCount());
            //2.判断商品是否存在 是否有库存 如果没有库存 卖完了
            /*if(seckillGoods==null || seckillGoods.getStockCount()<=0){
                throw new RuntimeException("卖完了");
            }*/
            //3.创建预订单在redis中
            SeckillOrder seckillOrder = new SeckillOrder();
            seckillOrder.setId(idWorker.nextId());//主键
            seckillOrder.setSeckillId(seckillGoods.getId());//秒杀商品的ID
            seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价
            seckillOrder.setUserId(username);//用户名
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");//未支付

            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_ORDER_KEY).put(username,seckillOrder);//key 用户  value:订单对象

            //4.减库存
            seckillGoods.setStockCount(seckillGoods.getStockCount()-1);

            //5.判断库存是否为0 为0  更新到数据库
            if(seckillGoods.getStockCount()<=0){
                //删除 redis的秒杀商品
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).delete(id);
                //更新到书库中
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            }else {
                //6.判断库存是否为0 不为0  重新设置回redis
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).put(id, seckillGoods);
            }
            System.out.println("5秒后库存为:"+seckillGoods.getStockCount());

            System.out.println("=======开始执行:"+new Date()+Thread.currentThread().getName());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("=======结束执行:"+new Date()+Thread.currentThread().getName());


            //更改用户抢单的状态了
            seckillStatus.setStatus(2);//待支付
            seckillStatus.setMoney(Float.valueOf(seckillOrder.getMoney()));
            seckillStatus.setOrderId(seckillOrder.getId());//订单ID

            redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).put(username,seckillStatus);
        }

    }
    public void clearQueue(SeckillStatus seckillStatus){
        //防止重复排队标识
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_QUEUE_REPEAT_KEY).delete(seckillStatus.getUsername());

        //清理抢单标示
        redisTemplate.boundHashOps(SystemConstants.SEC_KILL_USER_STATUS_KEY).delete(seckillStatus.getUsername());
    }

}
