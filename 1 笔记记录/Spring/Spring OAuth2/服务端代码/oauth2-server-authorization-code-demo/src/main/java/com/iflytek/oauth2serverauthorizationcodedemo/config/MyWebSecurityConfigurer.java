package com.iflytek.oauth2serverauthorizationcodedemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    /**
     * 不提供加密实现方式
     * 密码加密方式实现类，如果不提供，则会在获取token时失败，异常信息：
     * There is no PasswordEncoder mapped for the id "null"
     * @return
     */
    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
                //.and()
                //.formLogin()
                .and()
                .csrf().disable()
                .httpBasic();
    }


}
