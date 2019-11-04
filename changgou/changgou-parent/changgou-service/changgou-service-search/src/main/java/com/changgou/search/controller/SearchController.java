package com.changgou.search.controller;

import com.changgou.search.service.SearchService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.search.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;
    /**
     * 导入数据
     */
    @RequestMapping("/import")
    public Result importES(){
        searchService.importSku();//
        return new Result(true, StatusCode.OK,"成功");
    }

    /**
     * 根据参数的值 (搜索的条件) 执行查询 返回结果(封装到MAP中)
     * @param searchMap
     * @return
     */
    @GetMapping
    public Map search(@RequestParam(required = false) Map searchMap){
        return searchService.search(searchMap);
    }
}
