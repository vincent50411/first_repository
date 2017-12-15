package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by liuwens on 2017/12/14.
 */
@RestController
@RefreshScope
public class ConfigClientController
{
    @Value("${test.config.test}")
    private String testValue;

    @RequestMapping("/test")
    public String test()
    {
        return testValue;
    }

}
