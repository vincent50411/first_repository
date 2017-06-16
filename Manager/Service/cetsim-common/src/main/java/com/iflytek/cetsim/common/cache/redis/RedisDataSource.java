package com.iflytek.cetsim.common.cache.redis;

import redis.clients.jedis.ShardedJedis;

/**
 *
 */
public interface RedisDataSource {

    abstract ShardedJedis getRedisClient();

    void returnResource(ShardedJedis shardedJedis);

    void returnResource(ShardedJedis shardedJedis, boolean broken);
}
