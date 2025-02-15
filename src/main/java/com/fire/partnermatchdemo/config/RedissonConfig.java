package com.fire.partnermatchdemo.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedissonConfig {

    private String host;

    private String port;

    private String password;

    private int timeout;



    private int redissonDb;

    @Bean
    public RedissonClient redissonClient() {

        // 1. 创建配置
        Config config = new Config();
//        String redisAddress = "redis://127.0.0.1:6379";
        String redisAddress = String.format("redis://%s:%s", host, port);
        config.useSingleServer().setAddress(redisAddress).setDatabase(redissonDb).setPassword(password).setTimeout(timeout);

                // use "redis://" for Redis connection
                // use "valkey://" for Valkey connection
                // use "valkeys://" for Valkey SSL connection
                // use "rediss://" for Redis SSL connection
//                .addNodeAddress("redis://127.0.0.1:7181");

// or read config from file
//        config = Config.fromYAML(new File("config-file.yaml"));

        // 2. 创建实例
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);

        return redisson;

    }

}
