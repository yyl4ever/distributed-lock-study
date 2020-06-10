package pers.hurricane.distributed.lock.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

/**
 * @Author: hurricane
 * @Date: 2020-06-10 20:10
 * @Description:
 */
@Configuration
public class RedisLockConfiguration {
    @Value("${redis.lock.timeout:60000}")
    private long redisLockTimeout;

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockRegistry(redisConnectionFactory, "redis-lock", redisLockTimeout);
    }
}
