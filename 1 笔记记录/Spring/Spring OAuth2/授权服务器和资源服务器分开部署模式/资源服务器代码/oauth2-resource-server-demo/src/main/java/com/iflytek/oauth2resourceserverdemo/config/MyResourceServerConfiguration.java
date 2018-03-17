package com.iflytek.oauth2resourceserverdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.servlet.http.HttpServletResponse;

/**
 * 资源权限配置
 * 鉴权服务器和资源服务器分开部署模式(tokenstore采用JWT或者redis存储，内存存储不行)
 */
@Configuration
@EnableResourceServer
public class MyResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String SPARKLR_RESOURCE_ID = "apple";

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.tokenServices(tokenServices()).resourceId(SPARKLR_RESOURCE_ID);
    }
    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        //采用JWT生成token，参考jwt token生成规则
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }



    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .anonymous().disable()
                .authorizeRequests()
                .antMatchers("/userTest/**").hasAnyRole("CLIENT", "TEST_2")
                .anyRequest().authenticated()
                .and()
                .httpBasic();
    }




}
