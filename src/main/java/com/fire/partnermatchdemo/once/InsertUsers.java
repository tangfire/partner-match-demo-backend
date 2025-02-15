package com.fire.partnermatchdemo.once;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

import com.fire.partnermatchdemo.mapper.UserMapper;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.service.UserService;
import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
public class InsertUsers {


    @Resource
    private UserMapper userMapper;

    @Resource
    private UserService userService;

    private ExecutorService executorService = new ThreadPoolExecutor(60,1000,10, TimeUnit.MINUTES,new ArrayBlockingQueue<Runnable>(10000));



    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doInsertUser(){

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 1000;

        List<User> userList = new ArrayList<User>();

        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("假用户");
            user.setUserAccount("fakerfire");
            user.setAvatarUrl("https://th.bing.com/th/id/R.954c44c6e9897ac29e54c5f94304e96b?rik=zpVWbi3ka09M6g&pid=ImgRaw&r=0");
            user.setGender(1);
            user.setUserPassword("123456789");
            user.setEmail("6272131@qq.com");
            user.setUserStatus(0);
            user.setPhone("");
            user.setUserRole(0);
            user.setTags("[]");
            user.setProfile("");
//            userMapper.insert(user);
            userList.add(user);

        }
        userService.saveBatch(userList,100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());


    }


    /**
     * 并发批量插入用户
     */
//    @Scheduled(initialDelay = 5000,fixedRate = Long.MAX_VALUE)
    public void doConcurrencyInsertUser(){

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        final int INSERT_NUM = 100000;

        int batchSize = 5000;

        int j = 0;

        List<CompletableFuture<Void>> futureList =  new ArrayList<>();

        for (int i = 0;i<20;i++){
            List<User> userList = Collections.synchronizedList(new ArrayList<User>());
            while(true){
                j++;
                User user = new User();
                user.setUsername("假用户");
                user.setUserAccount("fakerfire");
                user.setAvatarUrl("https://th.bing.com/th/id/R.954c44c6e9897ac29e54c5f94304e96b?rik=zpVWbi3ka09M6g&pid=ImgRaw&r=0");
                user.setGender(1);
                user.setUserPassword("123456789");
                user.setEmail("6272131@qq.com");
                user.setUserStatus(0);
                user.setPhone("");
                user.setUserRole(0);
                user.setTags("[]");
                user.setProfile("");
//            userMapper.insert(user);
                userList.add(user);
                if (j % batchSize == 0){
                    break;
                }
            }
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                userService.saveBatch(userList, batchSize);

            },executorService);

            futureList.add(future);

        }

        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()])).join();


        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());

    }


}
