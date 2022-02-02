package org.example.bot.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class JedisConfig extends CachingConfigurerSupport {
    @Value("${jedis.host}")
    private String host;

    @Value("${jedis.port}")
    private int port;

    @Bean
    public JedisPool getJedis() {
        return new JedisPool(new JedisPoolConfig(), host, port);
    }

}