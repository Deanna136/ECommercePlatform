package com.example.ecommerceplatform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ECommercePlatformApplication {

    public static void main(String[] args) {
        System.out.println("成功启动项目！");
        SpringApplication.run(ECommercePlatformApplication.class, args);
    }

}
