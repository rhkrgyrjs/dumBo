package com.dumbo.repository.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Date;

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

    public void saveRefreshToken(String userId, String refreshToken, Date expDate)
    {
        String key = "RefreshToken:" + userId;
        syncCommands.set(key, refreshToken);
        long unixTimestampSeconds = expDate.getTime() / 1000;
        syncCommands.expireat(key, unixTimestampSeconds);
    }

    public String getRefreshToken(String userId)
    {
        String key = "RefreshToken:" + userId;
        return syncCommands.get(key); // 토큰 없으면 null 반환
    }

    public void deleteRefreshToken(String userId)
    {
        String key = "RefreshToken:" + userId;
        syncCommands.del(key);
    }
}
