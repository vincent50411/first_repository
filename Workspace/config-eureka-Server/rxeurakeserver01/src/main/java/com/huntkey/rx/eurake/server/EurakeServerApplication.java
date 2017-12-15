package com.huntkey.rx.eurake.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Created by licj on 2017/4/6.
 */

@SpringBootApplication
@EnableEurekaServer
public class EurakeServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurakeServerApplication.class,args);
    }
}
