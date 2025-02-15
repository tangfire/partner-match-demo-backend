package com.fire.partnermatchdemo.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fire.partnermatchdemo.model.domain.User;
import com.fire.partnermatchdemo.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

import java.util.Arrays;
import java.util.List;

/**
 * 缓存预热任务
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    // 重点用户
    private List<Long> mainUserList = Arrays.asList(4L);



    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 12 1 * * *")   //自己设置时间测试
     public void doCacheRecommendUser() {

        RLock lock = redissonClient.getLock("partnermatch:precachejob:docache:lock");

        try {
            // 只有一个线程能获得锁
            if (lock.tryLock(0,30000L,TimeUnit.MILLISECONDS)){

                //查数据库
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                Page<User> userPage = userService.page(new Page<>(1,20),queryWrapper);
                String redisKey = String.format("partnermatch:user:recommend:%s",mainUserList);
                ValueOperations valueOperations = redisTemplate.opsForValue();
                //写缓存,30s过期
                try {
                    valueOperations.set(redisKey,userPage,30000, TimeUnit.MILLISECONDS);
                } catch (Exception e){
                    log.error("redis set key error",e);
                }
            }


        } catch (InterruptedException e) {
            log.error("redis lock error",e);
        }finally {
            // 只能释放自己的锁
            // 要放在finally,不然前面报错了,锁还释放不掉
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
