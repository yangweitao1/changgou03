package com.changgou;

import com.changgou.goods.dao.BrandMapper;
import com.changgou.goods.pojo.Brand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou *
 * @since 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMybatis {

    @Autowired
    private BrandMapper brandMapper;

    //查询所有的品牌的列表
    @Test
    public void selectAll() {
        List<Brand> brands = brandMapper.selectAll();
        Brand brand = brandMapper.selectByPrimaryKey(1115);
        System.out.println(brand);
        System.out.println(brands.size());
    }

}
