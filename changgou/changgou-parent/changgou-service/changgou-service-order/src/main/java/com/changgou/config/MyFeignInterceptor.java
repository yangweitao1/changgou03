package com.changgou.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 在feign调用之前,拦截,添加头信息,放行,头传递给被调用的微服务了
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.config *
 * @since 1.0
 */
@Component
public class MyFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //1.获取网关传递过来的头信息,放在request对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            //2.获取所有的头信息
            HttpServletRequest request = requestAttributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();//所有的头信息名称
            //3.循环遍历头信息,将这些头信息 使用requestTemplate传给微服务
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();//头名称   headename(Authorization) --->headervalue(......)
                    String value = request.getHeader(name);//头信息
                    //传递头信息
                    requestTemplate.header(name, value);
                }
            }

        }
    }
}
