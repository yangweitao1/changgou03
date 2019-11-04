package com.changgou.filter;

import com.changgou.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.filter *
 * @since 1.0
 */
@Component
public class AuthorizeFilter implements GlobalFilter, Ordered {
    private static final String AUTHORIZE_TOKEN = "Authorization";

    //处理请求的
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        //1.获取request请求对象
        ServerHttpRequest request = exchange.getRequest();

        //2.获取response请求对象
        ServerHttpResponse response = exchange.getResponse();

        //3.判断当前的请求url 是否为登录的URL 放行
        String path = request.getURI().getPath();//获取到当前的URL的路径

        if (path.startsWith("/api/user/login")) {
            //放行
            return chain.filter(exchange);
        }

        //4.先获取(从头信息获取令牌,从cookie中获取令牌,从请求参数中获取令牌)令牌
        //4.1 从头中获取token
        String token = request.getHeaders().getFirst(AUTHORIZE_TOKEN);

        if (StringUtils.isEmpty(token)) {

            //4.2 从cookie中获取 key:AUTHORIZE_TOKEN
            HttpCookie first = request.getCookies().getFirst(AUTHORIZE_TOKEN);
            if (first != null) {
                token = first.getValue();//赋值给令牌变量
            }
        }

        if (StringUtils.isEmpty(token)) {
            //4.3 从请求参数中获取token
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }

        if (StringUtils.isEmpty(token)) {
            //token获取不到,没权限 直接返回  重定向到登录的页面
            //设置状态码为303 302
            response.setStatusCode(HttpStatus.SEE_OTHER);
            //设置响应头信息 设置重定向的路径
            response.getHeaders().set("Location","http://localhost:9001/oauth/login?FROM="+request.getURI().toString());
            //返回
            return response.setComplete();
        }


        //5.用户JWT解析,解析成功放行,否则直接返回

        try {
            // Claims claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            //解析失败 直接返回
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //放行  需要接收用户的token令牌信息 传递给路由到的微服务中.
        request.mutate().header(AUTHORIZE_TOKEN, "Bearer "+token);


        return chain.filter(exchange);
    }

    //过滤器的执行顺序的值的设置
    @Override
    public int getOrder() {
        return 0;
    }
}
