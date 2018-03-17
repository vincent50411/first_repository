package com.iflytek.oauth2serverauthorizationcodedemo.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/web")
public class WebOAuth2Controller {



    @RequestMapping("/test")
    public String getTest()
    {


        return "我是测试用的接口";
    }


}
