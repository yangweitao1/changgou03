package com.changgou.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 接收请求,返回一个页面(模板)
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.oauth.controller *
 * @since 1.0
 */
@Controller
@RequestMapping("/oauth")
public class LoginRedirectController {
    @RequestMapping("/login")
    public String login(String FROM ,Model model) {
        model.addAttribute("from",FROM);
        return "login";
    }
}
