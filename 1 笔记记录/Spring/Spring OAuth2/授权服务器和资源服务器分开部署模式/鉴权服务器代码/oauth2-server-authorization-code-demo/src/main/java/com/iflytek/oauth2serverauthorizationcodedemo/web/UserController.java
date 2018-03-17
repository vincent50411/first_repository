package com.iflytek.oauth2serverauthorizationcodedemo.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {


    @RequestMapping("/info")
    public String getUserInfo()
    {
        return "返回用户信息接口";
    }

}
