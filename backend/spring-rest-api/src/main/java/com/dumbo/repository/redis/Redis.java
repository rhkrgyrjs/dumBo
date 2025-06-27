package com.dumbo.repository.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Redis(Lettuce) CRUD 기능 Mapper
 */
public class Redis 
{
    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> connection;
    private RedisCommands<String, String> syncCommands;
    
    public Redis(String host, int port, String password, int database)
    {
        String url = String.format("redis://:%s@%s:%d/%d", password, host, port, database);
        redisClient = RedisClient.create(url);
        connection = redisClient.connect();
        syncCommands = connection.sync();
    }

    public void set(String key, String value, long ttlSecond)
    {
        syncCommands.set(key, value);
        syncCommands.expireat(key, ttlSecond); // ms단위가 아닌 s(초) 단위
    }

    public String get(String key) { return syncCommands.get(key); }
    public void delete(String key) { syncCommands.del(key); }
}
