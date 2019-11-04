package com.changgou.oauth.service.impl;

import com.changgou.oauth.service.UserLoginService;
import com.changgou.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.oauth.service.impl *
 * @since 1.0
 */
@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient balancerClient;

    @Override
    public AuthToken login(String username, String password, String clientId, String secret, String grantType) {

        //模拟POST发送请求申请令牌 传递 5个参数过去(clientid secret,username,password,grant_type)

        //choose 里面的参数 指定的微服务的名称 spring.application.name指定的值
        String  url = balancerClient.choose("user-auth").getUri().toString()+"/oauth/token";


        //1.设置头信息
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        String s = clientId + ":" + secret;
        headers.add("Authorization","Basic "+Base64.getEncoder().encodeToString(s.getBytes()));
        //2.设置请求体信息
        MultiValueMap<String,String> requestbody  = new LinkedMultiValueMap<>();
        requestbody.add("grant_type",grantType);
        requestbody.add("username",username);
        requestbody.add("password",password);

        //3.执行发送请求(POST )
        HttpEntity<MultiValueMap<String,String>> requestEntity = new HttpEntity<MultiValueMap<String,String>>(requestbody,headers);
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

        //4.获取到MAP
        Map body = exchange.getBody();

        //将响应数据封装成AuthToken对象
        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String accessToken = (String) body.get("access_token");
        //刷新令牌(jwt)
        String refreshToken = (String) body.get("refresh_token");
        //jti，作为用户的身份标识
        String jwtToken= (String) body.get("jti");
        authToken.setJti(jwtToken);
        authToken.setAccessToken(accessToken);
        authToken.setRefreshToken(refreshToken);
        return authToken;
    }

    public static void main(String[] args) {
        String aaa="Y2hhbmdnb3U6Y2hhbmdnb3U=";//就是base64加密后的数据
        byte[] decode = Base64.getDecoder().decode(aaa.getBytes());
        String s = new String(decode);
        System.out.println(s);


    }
}
