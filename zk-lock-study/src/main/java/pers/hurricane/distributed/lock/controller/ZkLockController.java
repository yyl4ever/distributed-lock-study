package pers.hurricane.distributed.lock.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * @Author: hurricane
 * @Date: 2020-06-10 21:11
 * @Description:
 */
@RestController
@RequestMapping
public class ZkLockController {
    private static final Logger logger = LoggerFactory.getLogger(ZkLockController.class);

    @Autowired
    private ZookeeperLockRegistry zookeeperLockRegistry;

    private static final String SUCCESS = "SUCCESS";

    private static final String FAIL = "FAIL";

    /**
     * 生产环境下，使用如下分布式锁，由于性能不是考量但要保证强一致性，所以使用具有 CP 特性的分布式 ZK 锁
     *
     * @throws InterruptedException
     */
    @GetMapping("/zookeeper")
    public String zookeeper() {
        long startTime = System.currentTimeMillis();
        Lock lock = zookeeperLockRegistry.obtain("zookeeper");

        boolean isLocked = lock.tryLock();
        if (isLocked) {
            try {
                logger.info("lock is ready");
                // 应用服务器宕机的情况下，zk 临时节点会自动删除
                TimeUnit.SECONDS.sleep(20);
                return SUCCESS;
            } catch (Exception e) {
                logger.error("出现异常:{}", e.getMessage(), e);
                logger.info("lock error, last time:{}", System.currentTimeMillis() - startTime);
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
            logger.info("锁已被他人占用");
            return FAIL;
        }

    }
}
