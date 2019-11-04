package com.changgou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou *
 * @since 1.0
 */
@SpringBootApplication
@EnableEurekaClient
public class GateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
    }

    //用于标识用户唯一标识 使用IP地址标识
    @Bean(name = "ipKeyResolver")
    KeyResolver ipKeyResolver() {

        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {
                //1.获取request请求
                ServerHttpRequest request = exchange.getRequest();
                String hostName = request.getRemoteAddress().getAddress().getHostName();//获取到的就是IP地址
                System.out.println("hostname:"+hostName);
                //2.获取rquest请求中的远程的ip地址
                return Mono.just(hostName);
            }
        };
    }
}
