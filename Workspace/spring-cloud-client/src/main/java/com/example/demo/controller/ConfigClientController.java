package com.example.demo.controller;

import com.example.demo.vo.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
        List<User> userList = new ArrayList<>();

        userList.add(new User("83dsfsdf", 20));
        userList.add(new User("3xxx", 23));
        userList.add(new User("3xxx", 43));
        userList.add(new User("3xxx", 13));
        userList.add(new User("4eee", 45));
        userList.add(new User("2dsfddesdf", 8));
        userList.add(new User("1dsfddesdf", 12));
        userList.add(new User("5dsfddesdf", 15));

        userList.stream().sorted(Comparator.comparing(User::getUserName).thenComparing(User::getAge)).forEach(item -> System.out.println(item.getUserName() + "; " + item.getAge()));

        return testValue;
    }

}
