package com.itheima.item.controller;

import com.itheima.item.service.PageService;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 模拟生成静态页
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.itheima.item.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/page")
public class PageController {

    @Autowired
    private PageService pageService;


    /**
     * spu的ID
     *
     * @return
     */
    @RequestMapping("/createHtml/{id}")
    public Result createHtml(@PathVariable(name = "id") Long id) {

        pageService.createPageHtml(id);
        return new Result(true, StatusCode.OK, "生成成功");
    }


}
