package com.huntkey.rx.zipkin.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import zipkin.server.EnableZipkinServer;

/**
 * Created by zhaomj on 2017/4/8.
 */
@SpringBootApplication
@EnableZipkinServer
public class ZipKinServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZipKinServerApplication.class,args);
    }
}
