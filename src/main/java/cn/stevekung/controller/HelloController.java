package cn.stevekung.controller;

import cn.stevekung.domain.User;
import cn.stevekung.redis.RedisService;
import cn.stevekung.redis.UserKey;
import cn.stevekung.result.CodeMsg;
import cn.stevekung.result.Result;
import cn.stevekung.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/result")
    @ResponseBody
    public Result<String> hello(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/hello")
    public String index(Model model){
        model.addAttribute("name", "thymeleaf");
        return "index";
    }

    @RequestMapping("/user")
    @ResponseBody
    public Result<User> user(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/tx")
    @ResponseBody
    public Result<Boolean> tx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis")
    @ResponseBody
    public Result<User> redis(){
        User u1 = redisService.get(UserKey.getById,""+2, User.class);
        return Result.success(u1);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User(2, "龚健2");
        redisService.set(UserKey.getById,""+2, user);
        return Result.success(true);
    }
}
