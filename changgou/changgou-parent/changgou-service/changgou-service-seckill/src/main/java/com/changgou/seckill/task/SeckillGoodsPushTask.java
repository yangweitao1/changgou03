package com.changgou.seckill.task;

import com.changgou.seckill.dao.SeckillGoodsMapper;
import com.changgou.seckill.pojo.SeckillGoods;
import entity.DateUtil;
import entity.SystemConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.seckill.task *
 * @since 1.0
 */
@Component
public class SeckillGoodsPushTask {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //每隔一段时间 调用一次该方法
    @Scheduled(cron = "0/5 * * * * ? ")// cron表达式:用于指定一个表达式,作用就是指定何时执行该方法
    public void pushGoodsToRedis() {

        List<Date> dateMenus = DateUtil.getDateMenus();//时间段数据

        for (Date dateMenu : dateMenus) {

            //获取年月日小时的字符串
            String time = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);


            //1.从数据库中查询符合条件的秒杀商品的数据
            /**
             *   select * from tb_seckill_goods where
             `status`='1'
             and stock_count>0
             and start_time>=开始时间段
             and end_time < 开始时间段+2hour
             and id not in (redis已有的id)
             */
            Example exmaple = new Example(SeckillGoods.class);
            Example.Criteria criteria = exmaple.createCriteria();
            criteria.andEqualTo("status", "1");
            criteria.andGreaterThan("stockCount", 0);
            criteria.andGreaterThanOrEqualTo("startTime", dateMenu);
            criteria.andLessThan("endTime", DateUtil.addDateHour(dateMenu, 2));//jota-time

            Set keys = redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).keys();
            // Set keys = redisTemplate.boundHashOps(time).keys();

            if (keys != null && keys.size() > 0) {
                criteria.andNotIn("id", keys);
            }

            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(exmaple);
            //2.将数据存储到redis中
            /**
             *
             * 			  key(2019100916)      商品id:1        商品的POJO1

             key(2019100916)      商品id:2        商品的POJO2

             key(2019100916)      商品id:3        商品的POJO4
             *
             */
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps(SystemConstants.SEC_KILL_GOODS_PREFIX + time).put(seckillGood.getId(), seckillGood);
                //设置过期时间
                redisTemplate.expireAt(SystemConstants.SEC_KILL_GOODS_PREFIX + time, DateUtil.addDateHour(dateMenu, 2));

                for (Integer i = 0; i < seckillGood.getStockCount(); i++) {
                    //创建队列,向队列中添加商品元素,每一个对的元素个数和库存数保持一致   key  :prifix_id  value: id
                    redisTemplate.boundListOps(SystemConstants.SEC_KILL_CHAOMAI_LIST_KEY_PREFIX+seckillGood.getId()).leftPush(seckillGood.getId());
                }
            }

        }

    }

    public static void main(String[] args) {

    }
}
