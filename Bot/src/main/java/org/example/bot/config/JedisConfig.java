package org.example.bot.config;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

public class JedisConfig {
    private static JedisPool pool;

    public static void init(Properties properties) {
        pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("jedis.host"), Integer.parseInt(properties.getProperty("jedis.port")));
    }

    public static Jedis getJedis() {
        return pool.getResource();
    }

    public static void destroy() {
        pool.destroy();
    }
}
