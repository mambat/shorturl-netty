package org.n3r.shorturl.redis;

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisClient implements Closeable {

    public JedisPool pool;

    /**
     * 生成多线程共享的使用的jedis对象。
     * @param redisConnect 诸如127.0.0.1:6379的HOST:PORT连接字符串
     */
    public RedisClient(String redisConnect) {
        HostPort hostPort = new HostPort(redisConnect, "127.0.0.1", 6379);
        pool = new JedisPool(new JedisPoolConfig(), hostPort.getHost(), hostPort.getPort());
    }

    public RedisClient(String host, int port) {
        pool = new JedisPool(new JedisPoolConfig(), host, port);
    }



    public String get(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.get(key);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public String set(String key, String value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.set(key, value);
        } finally {
            pool.returnResource(jedis);
        }
    }

    public String setex(String key, int seconds, String value) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.setex(key, seconds, value);
        } finally  {
            pool.returnResource(jedis);
        }
    }

    public Long incr(String key) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.incr(key);
        } finally  {
            pool.returnResource(jedis);
        }
    }

    public void expire(String key, int seconds) {
        Jedis jedis = pool.getResource();
        try {
            jedis.expire(key, seconds);
        }
        finally  {
            pool.returnResource(jedis);
        }
    }

    public Set<String> keys(String pattern) {
        Jedis jedis = pool.getResource();
        try {
            return jedis.keys(pattern);
        }
        finally  {
            pool.returnResource(jedis);
        }
    }

    public void close() throws IOException {
        pool.destroy();
    }

}
