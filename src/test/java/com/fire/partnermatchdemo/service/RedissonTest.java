package com.fire.partnermatchdemo.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    void test(){

        // list 数据存在本地 JVM 内存中
        List<String> list = new ArrayList<>();
        list.add("fire");
        System.out.println("list: "+ list.get(0));
//        list.remove(0);

        // 数据存在 redis 的内存中
        RList<Object> rList = redissonClient.getList("test-list");

        rList.add("fire");
        System.out.println("rList: " + rList.get(0));
//        rList.remove(0);

        // map
        Map<String,Integer> map = new HashMap<>();
        map.put("fire",1);
        System.out.println("map: "+ map.get("fire"));

        RMap<Object, Object> map1 = redissonClient.getMap("test-map");
        map1.put("fire",1);
        System.out.println("map1: "+ map1.get("fire"));

    }



    @Test
    void testWatchDog() {
        RLock lock = redissonClient.getLock("partnermatch:precachejob:docache:lock");
        try {
            // 只有一个线程能获取到锁
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Thread.sleep(300000);//todo 实际要执行的代码
                System.out.println("getLock: " + Thread.currentThread().getId());
            }
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } finally {
            // 只能释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

}
