package com.fire.partnermatchdemo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.fire.partnermatchdemo.mapper")
@EnableScheduling
public class PartnerMatchDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PartnerMatchDemoApplication.class, args);
    }

}
