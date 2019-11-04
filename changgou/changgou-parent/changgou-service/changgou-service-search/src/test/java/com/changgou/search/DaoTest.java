package com.changgou.search;

import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import org.joda.time.field.SkipUndoDateTimeField;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search *
 * @since 1.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class DaoTest {

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Test
    public void importES(){
        SkuInfo skuInfo = new SkuInfo();
        skuInfo.setId(1000L);
        skuInfo.setName("华为手机测试手机");
        skuInfo.setBrandName("华为");
        skuInfo.setCategoryName("手机");
        skuInfo.setPrice(999L);
        skuEsMapper.save(skuInfo);
    }
}
