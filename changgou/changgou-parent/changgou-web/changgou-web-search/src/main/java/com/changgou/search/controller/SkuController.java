package com.changgou.search.controller;

import com.changgou.search.feign.SkuFeign;
import com.changgou.search.pojo.SkuInfo;
import entity.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search.controller *
 * @since 1.0
 */
@Controller
@RequestMapping("/search")
public class SkuController {

    @Autowired
    private SkuFeign skuFeign;


    /**
     * @RequestBody(required = false) Map searchMap,
     * 根据条件查询商品的列表数据进行渲染
     */
    @GetMapping("/list")
    public String searchList(@RequestParam(required = false) Map searchMap, Model model) {
        //1.接收请求
        //2.调用feign查询数据
        Map search = skuFeign.search(searchMap);

        //3.渲染结果
        model.addAttribute("result", search);


        //4.搜索条件的数据回显
        model.addAttribute("searchMap", searchMap);//{"keywords":"华为","brand":"华为"}


        //5.设置接收到的url  写一个方法 用于解析搜索的条件 拼接URL 返回给页面
        model.addAttribute("url", url(searchMap));

        //6.设置分页查询封装的对象 返回给页面 pageNum pageSize total

        Page<SkuInfo> infoPage = new Page<SkuInfo>(
                Long.valueOf(search.get("total").toString()),
                Integer.valueOf(search.get("pageNum").toString()),
                Integer.valueOf(search.get("pageSize").toString())
        );// 上一页 下一页 左边开始的页码 右边结束的页码 总记录数  总页数

        model.addAttribute("page",infoPage);

        return "search";
    }

    private String url(Map<String, String> searchMap) {// "keywords=shouji&name
        String url = "/search/list";//
        if (searchMap != null && searchMap.size() > 0) {
            url += "?";
            for (Map.Entry<String, String> stringStringEntry : searchMap.entrySet()) {
                String key = stringStringEntry.getKey();//keywords brand
                String value = stringStringEntry.getValue();//手机  华为
                //如果是pageNum的key 不需要拼
                if(key.equalsIgnoreCase("pageNum")){
                    continue;
                }
                url += key + "=" + value + "&";
            }
            url=url.substring(0, url.length() - 1);//去掉最后一个&
        }

        return url;
    }
}
