package com.fire.partnermatchdemo.service;
import java.util.Date;

import com.fire.partnermatchdemo.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        // 增

        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("fireString","dog");
        valueOperations.set("fireInt",1);
        valueOperations.set("fireDouble",2.0);
        User user = new User();
        user.setId(123L);
        user.setUsername("fireshine");

        valueOperations.set("fireUser",user);

        // 查
        Object o = valueOperations.get("fireString");

        Assertions.assertTrue("dog".equals((String)o));
        Object o1 = valueOperations.get("fireInt");
        Assertions.assertTrue(1 == (Integer)o1);


        Object o2 = valueOperations.get("fireDouble");

        Assertions.assertTrue(2.0 == (Double)o2);

        System.out.println(valueOperations.get("fireUser"));


    }


}
