package pers.hurricane.distributed.lock.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.zookeeper.config.CuratorFrameworkFactoryBean;
import org.springframework.integration.zookeeper.lock.ZookeeperLockRegistry;

/**
 * @Author: yangyl
 * @Date: 2020-06-02 10:53
 * @Description:
 */
@Configuration
public class ZookeeperLockConfiguration {
    @Value("${zookeeper.host}")
    private String zkUrl;

    @Bean
    public CuratorFrameworkFactoryBean curatorFrameworkFactoryBean() {
        return new CuratorFrameworkFactoryBean(zkUrl);
    }

    @Bean
    public ZookeeperLockRegistry zookeeperLockRegistry(CuratorFramework curatorFramework) {
        return new ZookeeperLockRegistry(curatorFramework, "/zookeeper-lock");
    }
}
