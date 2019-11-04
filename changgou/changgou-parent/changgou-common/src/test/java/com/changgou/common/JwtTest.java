package com.changgou.common;

import com.alibaba.fastjson.JSON;
import entity.JwtUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.common *
 * @since 1.0
 */
public class JwtTest {

    //创建JWT数据(颁发令牌)
    @Test
    public void createJwt() {
        //1.创建buidler
        JwtBuilder builder = Jwts.builder();
        //2.创建头 有默认
        //3.创建载荷
        builder
                .setIssuer("chuangzhiboke")//签发者
                .setId("weiyibiaoshi")//唯一的标识
                .setSubject("小白")//主题 可以是JSON格式的数据
                .setIssuedAt(new Date())//签发的日期
                //.setExpiration(new Date())//设置令牌的有效期的时间 有效期至 现在
                //4.创建签名 设置签名的算法,设置秘钥(secret)

                .signWith(SignatureAlgorithm.HS256,"itcast");

        //自定义载荷,加入JWT中
        Map<String, Object> map = new HashMap<>();
        map.put("id","1111");
        map.put("username","张三疯");
        map.put("age",1111);
        builder.addClaims(map);


       //5.组合成 JWT数据
        String compact = builder.compact();

        System.out.println(compact);


    }

    //解析JWT数据
    @Test
    public void parseJwt() {
        String compact="eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJjaHVhbmd6aGlib2tlIiwianRpIjoid2VpeWliaWFvc2hpIiwic3ViIjoi5bCP55m9IiwiaWF0IjoxNTY5MzE4MTg1LCJpZCI6IjExMTEiLCJhZ2UiOjExMTEsInVzZXJuYW1lIjoi5byg5LiJ55avIn0.m_84NDBT7HkYamcVOpJq2WPrgjsfOZT3Ntbvk4s9NEk";
        Jws<Claims> itcast = Jwts.parser()
                //设置秘钥
                .setSigningKey("itcast")

                .parseClaimsJws(compact);
        Claims body = itcast.getBody();
        System.out.println(body);
    }

    @Test
    public void base64xxx(){
        String itcast = Base64.getEncoder().encodeToString(new String("itcast").getBytes());
        String itcasts = Base64.getEncoder().encodeToString(new String("itcasts").getBytes());
        byte[] itcasts1 = TextCodec.BASE64.decode("itcast");
        String string = new String(itcasts1,Charset.forName("gbk"));
        byte[] itcasts2 = TextCodec.BASE64.decode("itcastsssss");
        String string2 = new String(itcasts2, Charset.forName("gbk"));
        System.out.println(string+":"+string2);
    }

    @Test
    public void jwtutilCreate(){
        Map<String,String>    map = new HashMap<>();
        map.put("id","1");
        map.put("age","222");
        map.put("role","admin");
        String jsonString = JSON.toJSONString(map);
        String jwt = JwtUtil.createJWT("13241231", jsonString, null);
        System.out.println(jwt);
    }

    @Test
    public void parseJwtUtil(){
       String compat = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMzI0MTIzMSIsInN1YiI6IntcInJvbGVcIjpcImFkbWluXCIsXCJpZFwiOlwiMVwiLFwiYWdlXCI6XCIyMjJcIn0iLCJpc3MiOiJhZG1pbiIsImlhdCI6MTU2OTMxODk0MSwiZXhwIjoxNTY5MzIyNTQxfQ.4dQNf4uoHPKR3ODLD9pneiGdtmy9Vi1kiXBIMu6fBhE";
        try {
            Claims claims = JwtUtil.parseJWT(compat);
            System.out.println(claims.getSubject());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
