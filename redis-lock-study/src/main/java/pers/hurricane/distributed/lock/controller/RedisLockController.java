package pers.hurricane.distributed.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @Author: hurricane
 * @Date: 2020-06-10 20:36
 * @Description: Redis 分布式锁实践
 */
@RestController
@RequestMapping
public class RedisLockController {

    private static final Logger logger = LoggerFactory.getLogger(RedisLockController.class);

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    private static final String SUCCESS = "SUCCESS";

    private static final String FAIL = "FAIL";

    /**
     * 同步操作
     *
     * @return
     */
    @GetMapping("/syncGoods")
    public String syncGoods() {
        long startTime = System.currentTimeMillis();
        // registryKey 和 lockKey 自动冒号连接，值为 uuid
        Lock lock = redisLockRegistry.obtain("sync:lock");

        boolean isLocked = lock.tryLock();
        if (isLocked) {
            try {
                // 模拟耗时操作
                TimeUnit.SECONDS.sleep(20);
                logger.info("congratulations, syncGoods successful, last time:{}", System.currentTimeMillis() - startTime);
                return SUCCESS;
            } catch (Exception e) {
                logger.error("同步出现异常:{}", e.getMessage(), e);
                logger.info("syncGoods error, last time:{}", System.currentTimeMillis() - startTime);
                return FAIL;
            } finally {
                try {
                    lock.unlock();
                    logger.info("分布式锁释放成功");
                } catch (Exception e) {
                    logger.error("分布式锁释放异常:{}", e.getMessage(), e);
                }
            }
        } else {
            logger.warn("分布式锁已被他人占用");
            return FAIL;
        }
    }
}
