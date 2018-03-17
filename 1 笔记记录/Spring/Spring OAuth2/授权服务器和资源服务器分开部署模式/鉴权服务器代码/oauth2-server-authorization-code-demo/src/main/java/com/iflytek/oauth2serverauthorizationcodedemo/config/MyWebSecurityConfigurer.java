package com.iflytek.oauth2serverauthorizationcodedemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyWebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    // 这个方法名是随便起的
    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        //暂时使用基于内存的AuthenticationProvider，在弹出的默认登陆页输入下边的用户名和密码
        auth.inMemoryAuthentication().withUser("user").password("1").roles("CLIENT");
    }

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
                //.antMatchers("/web/**").hasRole("TEST")
                //.antMatchers("/user/**", "/userTest/**").hasAnyRole("TEST_2")
                .and()
                .csrf().disable()
                .anonymous().disable()

                .httpBasic();
    }


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     *  // specified multiple URL patterns that any user can access.
     // any user can access a request if the URL starts with "/resources/", equals "/login.html", or equals "/about".
     .antMatchers("/resources/**", "/login.html", "/about").permitAll()
     // Any URL that starts with "/admin/" will be resticted to users who have the role "ROLE_ADMIN". You will notice that since we are invoking the hasRole method we do not need to specify the "ROLE_" prefix.
     .antMatchers("/admin/**").hasRole("ADMIN")
     // Any URL that starts with "/db/" requires the user to have both "ROLE_ADMIN" and "ROLE_DBA".
     .antMatchers("/db/**").access("hasRole('ADMIN') and hasRole('DBA')")
     // Any URL that has not already been matched on only requires that the user be authenticated
     .anyRequest().authenticated()
     */

    /*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                //个人首页只允许拥有MENBER,SUPER_ADMIN角色的用户访问
                .antMatchers("/me").hasAnyRole("MEMBER","SUPER_ADMIN")
                .anyRequest().authenticated()
                .and()
                    .formLogin()
                    //这里程序默认路径就是登陆页面，允许所有人进行登陆
                    .loginPage("/").permitAll()　　　　　　　　　　　　　　　　　　
                    .loginProcessingUrl("/log")　　　　　　　　　　　　　　　　　　//登陆提交的处理url
                    .failureForwardUrl("/?error=true")　　　　　　　　　　　　　　//登陆失败进行转发，这里回到登陆页面，参数error可以告知登陆状态
                    .defaultSuccessUrl("/me")　　　　　　　　　　　　　　　　　　　　//登陆成功的url，这里去到个人首页
                .and()
                    .logout()
    　　　　　　　　 .logoutUrl("/logout").permitAll()
    　　　　　　　　 .logoutSuccessUrl("/?logout=true")　　　　//按顺序，第一个是登出的url，security会拦截这个url进行处理，所以登出不需要我们实现，第二个是登出url，logout告知登陆状态
                .and()
                    .rememberMe()
                    .tokenValiditySeconds(604800)　　　　　　　　　　　　　　　　//记住我功能，cookies有限期是一周
                    .rememberMeParameter("remember-me")　　　　　　　　　　　　　　//登陆时是否激活记住我功能的参数名字，在登陆页面有展示
                    .rememberMeCookieName("workspace");　　　　　　　　　　　　//cookies的名字，登陆后可以通过浏览器查看cookies名字
    }*/

}
