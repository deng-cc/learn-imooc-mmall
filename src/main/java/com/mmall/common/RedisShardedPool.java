package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * RedisShardedPool.
 *
 * @author Dulk
 * @version 20190215
 * @date 2019/2/15
 */
public class RedisShardedPool {
    /**
     * jedis连接池
     */
    private static ShardedJedisPool pool;
    /**
     * 最大连接数
     */
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));
    /**
     * 最大空闲数
     */
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));
    /**
     * 最小空闲数
     */
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));
    /**
     * 在借用一个jedis实例时，是否要进行验证操作
     * <p>
     * 如果赋值true，则得到的jedis实例肯定是可用的
     * </p>
     */
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    /**
     * 在归还一个jedis实例时，是否要进行验证操作
     * <p>
     * 如果赋值true，则放回jedispool的jedis实例肯定是可以用的
     * </p>
     */
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));

    /**
     * redis的ip地址
     */
    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    /**
     * redis的端口号
     */
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));
    /**
     * redis的ip地址
     */
    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    /**
     * redis的端口号
     */
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();

        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，false抛出异常，true阻塞直到超时，默认为true

        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);

        List<JedisShardInfo> jedisShardInfoList = new ArrayList<JedisShardInfo>(2);
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool = new ShardedJedisPool(config, jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);


    }

    static {
        initPool();
    }

    /**
     * 从连接池中获取一个jedis连接实例
     */
    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for(int i = 0; i < 10; i++) {
            jedis.set("key" + i, "value" + i);
        }
        returnResource(jedis);
        System.out.println("end");
    }

}
