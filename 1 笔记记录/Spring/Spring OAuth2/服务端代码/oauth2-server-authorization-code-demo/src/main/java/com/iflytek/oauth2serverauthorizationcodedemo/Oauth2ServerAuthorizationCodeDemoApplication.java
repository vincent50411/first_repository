package com.iflytek.oauth2serverauthorizationcodedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication
@EnableAuthorizationServer
public class Oauth2ServerAuthorizationCodeDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2ServerAuthorizationCodeDemoApplication.class, args);
	}


}
