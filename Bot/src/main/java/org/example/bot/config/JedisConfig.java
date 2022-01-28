package org.example.bot.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisConfig {
    private static final JedisPool pool = new JedisPool(new JedisPoolConfig(), "127.0.0.1", 6379);

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void destroy() {
        pool.destroy();
    }
}
