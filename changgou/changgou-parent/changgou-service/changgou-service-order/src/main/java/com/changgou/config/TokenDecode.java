package com.changgou.config;

import com.alibaba.fastjson.JSON;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.config *
 * @since 1.0
 */
@Component
public class TokenDecode {

    //定义一个方法 用户获取用户信息  {"username":"zhangsan"}
    public Map<String, String> getUserInfo() {
        //1.获取令牌信息
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        String tokenValue = details.getTokenValue();
        //2.解析令牌
        String pubKey = getPubKey();
        Jwt jwt = JwtHelper.decodeAndVerify(tokenValue, new RsaVerifier(pubKey));
        //3.获取令牌中的用户信息
        String claims = jwt.getClaims();//json数据
        return JSON.parseObject(claims,Map.class);//
    }

    private static final String PUBLIC_KEY = "public.key";
    private String getPubKey() {
        Resource resource = new ClassPathResource(PUBLIC_KEY);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(resource.getInputStream());
            BufferedReader br = new BufferedReader(inputStreamReader);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return null;
        }
    }
}
