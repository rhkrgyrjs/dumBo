package com.dumbo.repository.dao.daoImpl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;

import com.dumbo.repository.dao.TokenDao;
import com.dumbo.repository.redis.Redis;

public class TokenDaoImpl implements TokenDao
{
    @Autowired
    private Redis redis;

    private String refreshTokenPreposition = "RefreshToken:";

    public void saveRefreshToken(String userId, String refreshToken, Date expDate)
    {
        redis.set(refreshTokenPreposition + userId, refreshToken, expDate.getTime() / 1000); // ms -> s 단위로 변환
    }


    public String getRefreshToken(String userId)
    {
        return redis.get(refreshTokenPreposition + userId); // 토큰 없으면 null 반환
    }


    public void deleteRefreshToken(String userId)
    {
        redis.delete(refreshTokenPreposition + userId);
    }
}
