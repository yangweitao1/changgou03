package com.changgou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.goods.pojo.Sku;
import com.changgou.search.dao.SkuEsMapper;
import com.changgou.search.pojo.SkuInfo;
import com.changgou.search.service.SearchService;
import entity.Result;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import springfox.documentation.spring.web.json.Json;

import java.util.*;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search.service.impl *
 * @since 1.0
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private SkuFeign skuFeign;

    @Autowired
    private SkuEsMapper skuEsMapper;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public void importSku() {
        //1.调用商品微服务的feign查询符合条件的SKU的列表数据
        Result<List<Sku>> skuresult = skuFeign.findByStatus("1");
        List<Sku> data = skuresult.getData();
        //2,需要将SKUPOJO 转成SKUinfo的POJO的数据
        List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(data), SkuInfo.class);
        for (SkuInfo skuInfo : skuInfos) {
            String spec = skuInfo.getSpec();//{"电视音响效果":"环绕","电视屏幕尺寸":"20英寸","尺码":"175"}
            Map<String, String> map = JSON.parseObject(spec, Map.class);
            skuInfo.setSpecMap(map);//
        }
        //3调用esclient保存到ES服务器中
        skuEsMapper.saveAll(skuInfos);
    }


    @Override
    public Map search(Map<String, String> searchMap) {
        //1.先获取关键字的值
        String keywords = searchMap.get("keywords");
        if (StringUtils.isEmpty(keywords)) {
            //2.判断是否为空 如果为空给一个默认的值 华为
            keywords = "华为";
        }
        //3.创建查询对象的 构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        //设置分组条件  商品[分类]的分组
        // select category_name from tb_sku where name like '%华为%' group by category_name
        //默认只返回10个数据
        // terms 参数:指定分组的别名  group by
        //field 设置分组的字段 category_name
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuCategorygroup").field("categoryName").size(100));

        //设置商品的[品牌]的分组条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuBrandgroup").field("brandName").size(100));

        //设置商品的[spec]的分组条件
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("skuSpecgroup").field("spec.keyword").size(10000));


        //4.设置查询的条件

        //4.1 设置过滤查询  多条件组合查询  boolean查询  MUST MUST_NOT  SHOULD filter
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //设置商品分类的过滤查询
        String category = searchMap.get("category");
        if (!StringUtils.isEmpty(category)) {

            boolQueryBuilder.filter(QueryBuilders.termQuery("categoryName", category));
        }
        //设置商品的品牌的过滤查询
        String brand = searchMap.get("brand");
        if (!StringUtils.isEmpty(brand)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("brandName", brand));
        }
        //设置规格的过滤查询    specMap.规格名.keyword=被点击到的规格选项的值      请求体:{"brand":"TCL","spec_网络制式":"电信2G"}

        for (String key : searchMap.keySet()) {
            if (key.startsWith("spec_")) {//获取规格的数据
                boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key.substring(5) + ".keyword", searchMap.get(key)));
            }
        }
        //价格区间的过滤查询  请求:{"price":"5000-*"}
        String price = searchMap.get("price");
        if (!StringUtils.isEmpty(price)) {
            String[] split = price.split("-");
            //   5000=>price >=300
            if (!split[1].equalsIgnoreCase("*")) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0], true).to(split[1], true));
            } else {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
            }
        }


        //设置过滤查询
        nativeSearchQueryBuilder.withFilter(boolQueryBuilder);


        //设置排序
        String sortField = searchMap.get("sortField");//要排序的字段 price
        String sortRule = searchMap.get("sortRule");//要排序的类型(DESC ASC)
        if(!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)){
            nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(sortRule.equalsIgnoreCase("ASC")?SortOrder.ASC:SortOrder.DESC));
            //nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort(sortField).order(SortOrder.valueOf(sortRule)));
        }

        //设置分页

        String pageNumString = searchMap.get("pageNum");//获取当前页码的值 1
        Integer pageNum=1;
        if(StringUtils.isEmpty(pageNumString)){
            pageNum=1;
        }else {
            pageNum = Integer.valueOf(pageNumString);
        }
        //PageRequest.of(1,10) :参数1 表示当前页码,0 表示第一页 参数2 表示每页显示的行
        Integer pageSize=30;
        nativeSearchQueryBuilder.withPageable(PageRequest.of(pageNum - 1, pageSize));


        //设置高亮
        nativeSearchQueryBuilder.withHighlightFields(new HighlightBuilder.Field("name"));//高亮 商品名称
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));//前缀和后缀



        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name", keywords));


        //5.构建查询对象
        NativeSearchQuery query = nativeSearchQueryBuilder.build();

        //6.执行查询 需要自定义映射器(高亮数据获取)

        AggregatedPage<SkuInfo> skuInfos = elasticsearchTemplate.queryForPage(query, SkuInfo.class,new MyHightSearchResultMapperImpl());


        //获取分组的结果 [商品分类]
        List<String> categoryList = getStringsTermsByAggreate(skuInfos, "skuCategorygroup");

        //获取分组的结果 [品牌]
        List<String> brandList = getStringsTermsByAggreate(skuInfos, "skuBrandgroup");


        //获取分组的结果 [规格的列表数据]
        StringTerms skuSpecgroupStringTerms = (StringTerms) skuInfos.getAggregation("skuSpecgroup");

        Map<String, Set<String>> specMap = getStringSetMap(skuSpecgroupStringTerms);

        //7.获取结果 返回
        int totalPages = skuInfos.getTotalPages();//总页数
        long totalElements = skuInfos.getTotalElements();//总记录数
        List<SkuInfo> content = skuInfos.getContent();//当前页的集合



        Map<String, Object> resultMap = new HashMap<>();//结果集合

        resultMap.put("categoryList", categoryList);
        resultMap.put("brandList", brandList);
        resultMap.put("specMap", specMap);

        resultMap.put("totalPages", totalPages);
        resultMap.put("total", totalElements);
        resultMap.put("rows", content);
        resultMap.put("pageNum", pageNum);//当前的页码
        resultMap.put("pageSize", pageSize);//每页显示的行


        return resultMap;
    }

    //根据获取到的规格的分组结果 去重返回一个Map
    private Map<String, Set<String>> getStringSetMap(StringTerms stringTermsSpec) {
        // key :规格的名称
        // value :规格选项值的set集合Set<String>
        Map<String, Set<String>> specMap = new HashMap<String, Set<String>>();
        Set<String> specValues = new HashSet<>();//[]
        if (stringTermsSpec != null) {
            List<StringTerms.Bucket> buckets = stringTermsSpec.getBuckets();
            for (StringTerms.Bucket bucket : buckets) {
                // {"手机屏幕尺寸":"5.5寸","网络":"电信4G","颜色":"白","测试":"s11","机身内存":"128G","存储":"16G","像素":"300万像素"}
                String keyAsString = bucket.getKeyAsString();
                //转成map对象
                Map<String, String> map = JSON.parseObject(keyAsString, Map.class);

                for (Map.Entry<String, String> stringStringEntry : map.entrySet()) {
                    //获取key
                    String key = stringStringEntry.getKey();// 手机屏幕尺寸
                    //获取value
                    String value = stringStringEntry.getValue();// 5.5寸


                    specValues = specMap.get(key);

                    if (specValues == null) {
                        specValues = new HashSet<>();
                    }
                    specValues.add(value);//["5.5寸"]

                    specMap.put(key, specValues);
                }
            }
        }

        return specMap;
    }

    private List<String> getStringsTermsByAggreate(AggregatedPage<SkuInfo> skuInfos, String groupname) {
        StringTerms stringTermsCateogry = (StringTerms) skuInfos.getAggregation(groupname);
        List<String> list = new ArrayList<>();
        if (stringTermsCateogry != null) {
            List<StringTerms.Bucket> buckets = stringTermsCateogry.getBuckets();

            for (StringTerms.Bucket bucket : buckets) {
                String keyAsString = bucket.getKeyAsString();
                list.add(keyAsString);
            }
        }
        return list;
    }
}
