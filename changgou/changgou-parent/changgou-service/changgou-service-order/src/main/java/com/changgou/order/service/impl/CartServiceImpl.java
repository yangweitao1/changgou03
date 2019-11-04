package com.changgou.order.service.impl;

import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.goods.pojo.Spu;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.order.service.impl *
 * @since 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SpuFeign spuFeign;

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void add(Long id, Integer num, String username) {

        if(num<=0){
            //删除购物车
            redisTemplate.boundHashOps("Cart_"+username).delete(id);
            return ;
        }


        //1.根据SKU的ID 获取sku的数据
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        //2.根据SPU的ID 获取SPU的数据
        Long spuId = skuResult.getData().getSpuId();
        Result<Spu> spuResult = spuFeign.findById(spuId);
        Spu spu = spuResult.getData();

        //3.将数据存储到pojo:(tb_order_item) 存储到Redis中
        OrderItem orderItem = new OrderItem();//封装要的值
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(spuId);
        orderItem.setSkuId(id);
        orderItem.setName(sku.getName());//SKU的名称
        orderItem.setPrice(sku.getPrice());//单价
        orderItem.setNum(num);//购买的数量
        orderItem.setMoney(num*sku.getPrice());//小计
        orderItem.setPayMoney(num*sku.getPrice());//实付金额
        orderItem.setImage(sku.getImage());//图片地址

        redisTemplate.boundHashOps("Cart_"+username).put(id,orderItem);


    }

    @Override
    public List<OrderItem> list(String username) {
        List<OrderItem>  values = redisTemplate.boundHashOps("Cart_" + username).values();
        return values;
    }
}
