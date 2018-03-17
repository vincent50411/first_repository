package com.iflytek.oauth2demo2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication
@EnableAuthorizationServer
public class Oauth2Demo2Application {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2Demo2Application.class, args);
	}
}
