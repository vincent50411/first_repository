package com.iflytek.oauth2serverauthorizationcodedemo.controler;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory() // 使用in-memory存储
                .withClient("appid") // client_id
                .secret("123456") // client_secret
                .authorizedGrantTypes("authorization_code", "client_credentials", "password", "refresh_token", "implicit") // 该client允许的授权类型
                .scopes("app"); // 允许的授权范围
    }

}
