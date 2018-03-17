package com.iflytek.oauth2resourceserverdemo.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userTest")
public class UserTestController {

    @RequestMapping("/info")
    public String test()
    {


        return "水电费水电费水电费水电费水电费水电费";
    }

}
