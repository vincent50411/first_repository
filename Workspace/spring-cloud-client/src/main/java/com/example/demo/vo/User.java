package com.example.demo.vo;

/**
 * Created by liuwens on 2017/12/15.
 */
public class User {

    private String userName;

    private int age;

    public User(String value, int age)
    {
        this.userName = value;
        this.age = age;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName=userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age=age;
    }
}
