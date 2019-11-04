package com.changgou.user.controller;

import com.alibaba.fastjson.JSON;
import com.changgou.user.pojo.User;
import com.changgou.user.service.UserService;
import com.github.pagehelper.PageInfo;
import entity.BCrypt;
import entity.JwtUtil;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/****
 * @Author:admin
 * @Description:
 * @Date 2019/6/14 0:18
 *****/

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;

    /***
     * User分页条件搜索实现
     * @param user
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@RequestBody(required = false) User user, @PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页条件查询User
        PageInfo<User> pageInfo = userService.findPage(user, page, size);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * User分页搜索实现
     * @param page:当前页
     * @param size:每页显示多少条
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}")
    public Result<PageInfo> findPage(@PathVariable int page, @PathVariable int size) {
        //调用UserService实现分页查询User
        PageInfo<User> pageInfo = userService.findPage(page, size);
        return new Result<PageInfo>(true, StatusCode.OK, "查询成功", pageInfo);
    }

    /***
     * 多条件搜索品牌数据
     * @param user
     * @return
     */
    @PostMapping(value = "/search")
    public Result<List<User>> findList(@RequestBody(required = false) User user) {
        //调用UserService实现条件查询User
        List<User> list = userService.findList(user);
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */

    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        //调用UserService实现根据主键删除
        userService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 修改User数据
     * @param user
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody User user, @PathVariable String id) {
        //设置主键值
        user.setUsername(id);
        //调用UserService实现修改User
        userService.update(user);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /***
     * 新增User数据
     * @param user
     * @return
     */
    @PostMapping
    public Result add(@RequestBody User user) {
        //调用UserService实现添加User
        userService.add(user);
        return new Result(true, StatusCode.OK, "添加成功");
    }

    /***
     * 根据ID查询User数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    //注解修饰的方法:当用户 拥有 admin的角色才能执行该方法
//    @PreAuthorize(value="hasAuthority('seckill_list')")
    public Result<User> findById(@PathVariable String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }

    @GetMapping("/load/{id}")
    public Result<User> findLoadById(@PathVariable String id) {
        //调用UserService实现根据主键查询User
        User user = userService.findById(id);
        return new Result<User>(true, StatusCode.OK, "查询成功", user);
    }

    /***
     * 查询User全部数据
     * @return
     */
    @GetMapping
    public Result<List<User>> findAll() {
        //调用UserService实现查询所有User
        List<User> list = userService.findAll();
        return new Result<List<User>>(true, StatusCode.OK, "查询成功", list);
    }

    //登录
    @RequestMapping("/login")
    public Result login(String username, String password, HttpServletResponse response) {
        //1.先根据用户名获取用户信息
        User user = userService.findById(username);
        if (user == null) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或者密码失败");
        }
        //2.获取密码
        String passwordfromdb = user.getPassword();
        if (!BCrypt.checkpw(password, passwordfromdb)) {
            return new Result(false, StatusCode.LOGINERROR, "用户名或者密码失败");

        }
        //3.匹配密码是否正确 成功了 颁发令牌 给用户
        Map<String, Object> subject = new HashMap<>();//自定义的载荷
        subject.put("role", "USER");
        subject.put("username", username);
        subject.put("success", "success");
        String jwt = JwtUtil.createJWT(UUID.randomUUID().toString(), JSON.toJSONString(subject), null);
        //存储到COOKIE中,返回给前端页面 TODO
        Cookie cookie = new Cookie("Authorization", jwt);
        response.addCookie(cookie);
        return new Result(true, StatusCode.OK, "登录成功", jwt);
    }


    /**
     * 添加积分
     *
     * @param points
     * @param username
     * @return
     */
    @GetMapping("/points/add")
    public Result addPoints(@RequestParam(name = "points") Integer points, @RequestParam(name = "username") String username) {
        int i = userService.addPoints(points, username);
        if (i <= 0) {
            return new Result(false, StatusCode.ERROR, "添加积分失败");
        }
        return new Result(true, StatusCode.OK, "添加积分成功");
    }


    public static void main(String[] args) {

        String jiamihoude = "eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9";
        byte[] decode = Base64.getDecoder().decode(jiamihoude.getBytes());
        String decodess = new String(decode);
        System.out.println(decodess);
    }
}
