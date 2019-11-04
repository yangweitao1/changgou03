package com.changgou.oauth.controller;

import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.AuthToken;
import com.changgou.oauth.util.CookieUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.oauth.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/user")
public class UserLoginController {
    @Autowired
    private UserLoginService userLoginService;

    public static final String GRANT_TYPE="password";

    //客户端ID
    @Value("${auth.clientId}")
    private String clientId;

    //秘钥
    @Value("${auth.clientSecret}")
    private String clientSecret;

    //Cookie存储的域名
    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    //Cookie生命周期
    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    /**
     * 模拟POST 申请令牌
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping("/login")
    public Result<AuthToken> login(String username, String password) {
        //1.获取用户名获取密码

        AuthToken result =  userLoginService.login(username,password,clientId,clientSecret,GRANT_TYPE);
        //3.获取令牌数据(MAP)
        //4.返回 设置数据到cookie中
        saveCookie(result.getAccessToken());//令牌
        return new Result<AuthToken>(true, StatusCode.OK,"登录成功",result);//
    }
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        CookieUtil.addCookie(response,cookieDomain,"/","Authorization",token,cookieMaxAge,false);
    }
}
