package com.changgou.order.service;

import com.changgou.order.pojo.OrderItem;

import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.order.service *
 * @since 1.0
 */
public interface CartService {

    void add(Long id, Integer num, String username);

    /**
     * 根据用户的名获取该用户的所有的购物车数据
     *
     * @param username
     * @return
     */
    List<OrderItem> list(String username);
}
