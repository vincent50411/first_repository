package com.iflytek.oauth2serverauthorizationcodedemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class MyAuthorizationServerConfigurer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("123");
        return converter;
    }

    @Bean
    public TokenStore tokenStore() {
//        return new InMemoryTokenStore();
//        return new JdbcTokenStore(jdbcTokenDataSource());
        return new JwtTokenStore(accessTokenConverter());
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore())
                .authenticationManager(this.authenticationManager)
                .accessTokenConverter(accessTokenConverter());
    }
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }















    /**
     * 用来配置客户端详情服务（ClientDetailsService），客户端详情信息在这里进行初始化，
     * 你能够把客户端详情信息写死在这里或者是通过数据库来存储调取详情信息
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
                // 使用in-memory存储
                .inMemory()
                    // client_id  应用注册时的id
                    .withClient("appid")
                    //客户端拥有的角色，此处必须加固定前缀ROLE_，如果是授权码模式，还要给用户增加角色在security中设置
                    .authorities("ROLE_CLIENT")
                    // client_secret 秘钥
                    .secret("123456")
                    // 该client允许的授权类型 , "client_credentials", "password", "refresh_token", "implicit"
                    .authorizedGrantTypes("authorization_code")
                    //范围，客户端携带的必须一致，如果设置了
                    .scopes("read")
                .and()
                    // client_id
                    .withClient("testapp")
                    .authorities("ROLE_TEST")
                    // client_secret
                    .secret("123456")
                    // 该client允许的授权类型
                    .authorizedGrantTypes("client_credentials")
                    .scopes("read")
                .and()
                    // client_id
                    .withClient("testapp-2")
                    .authorities("ROLE_TEST_2")
                    // client_secret
                    .secret("123456")
                    // 该client允许的授权类型
                    .authorizedGrantTypes("client_credentials")
                    .scopes("read");
    }

    /**
     * 用来配置令牌端点(Token Endpoint)的安全约束
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

}
