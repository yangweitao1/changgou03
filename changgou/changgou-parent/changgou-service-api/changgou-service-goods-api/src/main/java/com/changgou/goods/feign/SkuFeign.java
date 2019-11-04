package com.changgou.goods.feign;

import com.changgou.goods.pojo.Sku;
import com.changgou.order.pojo.OrderItem;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.goods.feign *
 * @since 1.0
 */
@FeignClient(name="goods")
@RequestMapping("/sku")
public interface SkuFeign {

    /**
     * 根据商品的状态 查询sKU的列表数据
     * @param status
     * @return
     */
    @GetMapping("/status/{status}")
    Result<List<Sku>> findByStatus(@PathVariable(name="status") String status);


    /**
     * 根据条件 查询 sku的列表
     * @param sku
     * @return
     */
    @PostMapping(value = "/search" )
    public Result<List<Sku>> findList(@RequestBody(required = false) Sku sku);


    /**
     * 根据SKU的ID 获取SKU的数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Sku> findById(@PathVariable(name="id") Long id);

    @PostMapping("/decr/count")
    public Result decrCount(@RequestBody OrderItem orderItem);

}
