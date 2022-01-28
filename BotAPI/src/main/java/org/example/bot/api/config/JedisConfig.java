package org.example.bot.api.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig extends CachingConfigurerSupport {
    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);

    @Bean
    public static JedisPool getJedis() {
        return pool;
    }

    public static void destroy() {
        pool.destroy();
    }
}
