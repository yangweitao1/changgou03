package com.changgou.order.controller;

import com.changgou.config.TokenDecode;
import com.changgou.order.pojo.OrderItem;
import com.changgou.order.service.CartService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.order.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private TokenDecode tokenDecode;

    /**
     * 添加购物车
     *
     * @param id  sku的ID
     * @param num 购买的数量  给某一个用户购买
     * @return
     */
    @RequestMapping("/add")
    public Result add(Long id, Integer num) {
        //获取当前登录的用户名
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        String username = userInfo.get("username");

        cartService.add(id, num, username);
        return new Result(true, StatusCode.OK, "添加购物车成功");
    }

    /**
     * 获取当前登录的用户的购物车的列表数据
     *
     * @return
     */
    @RequestMapping("/list")
    public Result<List<OrderItem>> list() {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        String username = userInfo.get("username");
        List<OrderItem> orderItemList = cartService.list(username);
        return new Result<List<OrderItem>>(true, StatusCode.OK, "列表查询成功", orderItemList);
    }


}
