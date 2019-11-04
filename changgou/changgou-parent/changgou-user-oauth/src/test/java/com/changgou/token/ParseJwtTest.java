package com.changgou.token;

import org.junit.Test;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;

/*****
 * @Author: www.itheima
 * @Date: 2019/7/7 13:48
 * @Description: com.changgou.token
 *  使用公钥解密令牌数据
 ****/
public class ParseJwtTest {

    /***
     * 校验令牌
     */
    @Test
    public void testParseToken(){
        //令牌
        String token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhcHAiXSwibmFtZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MjAwMTY2MjU4NiwiYXV0aG9yaXRpZXMiOlsic2Vja2lsbF9saXN0IiwiZ29vZHNfbGlzdCJdLCJqdGkiOiJlZTJkZjZiYS04MGUwLTQyZWEtOWVkMi0zM2JhNWU5NGE3YTUiLCJjbGllbnRfaWQiOiJjaGFuZ2dvdSIsInVzZXJuYW1lIjoiemhhbmdzYW4ifQ.X9guYEx78x-XoGuq-ck4CuAWhW_El-FA2rrqi_2yAUd8N35jM9z2wla44Oa5gBZv_pYfIiDqhwbtfTUp5URRae7XirCk7d9oA8v_OJew-QalnhE5NOiIjHxQb-pQJOVdh5BsuPrnPylygErUy0UYv5EYKU6IZzTTO1GoqvRLFzEFnVXDt_V2L1uH7VpZ-gR4E9q9p0t1p23aL5uR8-FOhpueosEnG6itnsd1ym4d7ZsJHsZMZrNN0vsQmse6ymdB7PvQdHhXr9yHf6E4hmeftBtyPFlAc8TW8196pb503e-7_KDniIghfwu72tKwHmDD96kvk13nhVmsTsMJDN77zA";

        //公钥
        String publickey = "-----BEGIN PUBLIC KEY-----MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqat3uHZiCcUmornbnwOhFae/Cg40yv83r+pI5dyhO6BccWCJM4d+O+PuupwBmkQv1OfkXDqr25WMiJbWfmIpsjkxX9IstjOQvjPkRi3ZtYo1qjODtC8IFWsdSWJlxdmjgPaIoAMblIqn1CLDwl+RC1kQnLl5B189qGjNh6d6kCxW2c/7Xv+HfFEsubzT1Uy/mpnuEs6wzjNtAO6A71DYJahFKAmOWZzwUgaNtabYok7qpxWek4sixtRbGUDWn4L8mAFBpd9v84V3/jqXrAjnjMRh9n3mVinjCUFlKqfoSryrdzOPhYA5zNwInS8gzXOd6lBbtvmWRh9DsyjrBrZvOQIDAQAB-----END PUBLIC KEY-----";

        //校验Jwt
        Jwt jwt = JwtHelper.decodeAndVerify(token, new RsaVerifier(publickey));

        //获取Jwt原始内容
        String claims = jwt.getClaims();
        System.out.println(claims);
        //jwt令牌
        String encoded = jwt.getEncoded();
        System.out.println(encoded);
    }
}
