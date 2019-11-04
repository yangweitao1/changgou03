package com.changgou.user.feign;

import com.changgou.user.pojo.User;
import entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 描述
 *
 * @author www.itheima.com
 * @version 1.0
 * @package com.changgou.user.feign *
 * @since 1.0
 */
@FeignClient(name = "user")
@RequestMapping("/user")
public interface UserFeign {
    /**
     * 根据用户的名称获取用户的信息
     *
     * @param id
     * @return
     */
    @GetMapping("/load/{id}")
    public Result<User> findLoadById(@PathVariable(name = "id") String id);


    /**
     * 给指定的用户添加积分
     *
     * @param points
     * @param username
     * @return
     */
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name = "points") Integer points, @RequestParam(name = "username") String username);
}
