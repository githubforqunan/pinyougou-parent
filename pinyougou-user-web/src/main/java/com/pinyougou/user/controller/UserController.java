package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import entry.Result;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/register")
    public Result register(@RequestBody TbUser user, String smsCode){
        boolean flag = userService.checkCode(smsCode, user.getPhone());
        if(!flag){
            return new Result(false,"验证码不一致！");
        }
        try {
            userService.register(user);
            return new Result(true,"注册成功");
        }catch (Exception e){
            return new Result(false,"注册失败");
        }

    }

    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        try {
            boolean flag = userService.sendCode(phone);
            if(flag){
                return new Result(true,"发送成功");
            }else{
                return new Result(false,"发送失败");
            }

        }catch (Exception e){
            return new Result(false,"发送失败");
        }
    }
    
    
}
