package com.changgou.oauth.service;

import com.changgou.oauth.util.AuthToken;

import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.oauth.service *
 * @since 1.0
 */
public interface UserLoginService {
    /**
     * 返回 令牌数据
     *
     * @param username  用户的用户名
     * @param password  用户的密码
     * @param clientId  客户端ID
     * @param secret    秘钥
     * @param grantType 授权类型(密码模式)
     * @return
     */
    AuthToken login(String username, String password, String clientId, String secret, String grantType);
}
