package com.iflytek.oauth2resourceserverdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 权限设置
 * 鉴权服务器和资源服务器分开部署模式(tokenstore采用JWT或者redis存储，内存存储不行)
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        //暂时使用基于内存的AuthenticationProvider，在弹出的默认登陆页输入下边的用户名和密码
        auth.inMemoryAuthentication().withUser("user").password("1").roles("CLIENT");
    }

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/userTest/**").hasAnyRole("CLIENT", "TEST_2");
    }*/

}
