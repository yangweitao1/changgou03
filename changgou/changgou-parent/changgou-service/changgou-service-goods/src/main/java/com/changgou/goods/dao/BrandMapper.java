package com.changgou.goods.dao;
import com.changgou.goods.pojo.Brand;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/****
 * @Author:admin
 * @Description:Brand的Dao
 * @Date 2019/6/14 0:12
 *****/
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 根据商品的分类的ID 查询品牌的列表数据
     * @param categoryId
     * @return
     */
    @Select(value="SELECT b.* from tb_brand b,tb_category_brand tb where tb.category_id=#{categoryId} and b.id=tb.brand_id")
    List<Brand>  findByCategory(Integer categoryId);
}
