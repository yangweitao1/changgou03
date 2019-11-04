package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.search.pojo.SkuInfo;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义映射器(自己获取高亮的数据返回)
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search.service.impl *
 * @since 1.0
 */

public class MyHightSearchResultMapperImpl implements SearchResultMapper {
    @Override
    public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {

        //1.获取当前页的结果集
        List<T> content = new ArrayList<>();

        //2.获取分页的对象

        //3.搜索命中的总记录数
        SearchHits hits = response.getHits();

        if (hits == null || hits.getTotalHits() <= 0) {
            return new AggregatedPageImpl<T>(content);
        }

        //6.获取高亮的数据
        for (SearchHit hit : hits) {
            //获取当前的行的数据JOSN  当前的POJO数据
            String sourceAsString = hit.getSourceAsString();

            SkuInfo skuInfo = JSON.parseObject(sourceAsString, SkuInfo.class);

            //获取高亮的字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            //获取字段对应的高亮的数据
            //参数名 高亮的字段名 和之前设置的高亮的字段名保持一致
            HighlightField highlightField = highlightFields.get("name");
            if (highlightField != null) {
                StringBuffer stringBuffer = new StringBuffer();
                Text[] fragments = highlightField.fragments();
                for (Text fragment : fragments) {
                    String string = fragment.string();//高亮的数据
                    stringBuffer.append(string);
                }
                skuInfo.setName(stringBuffer.toString());//设置高亮数据
            }
            System.out.println(skuInfo.getName());
            content.add((T) skuInfo);
        }


        return new AggregatedPageImpl<T>(content, pageable, hits.getTotalHits(), response.getAggregations(), response.getScrollId());
    }
}
