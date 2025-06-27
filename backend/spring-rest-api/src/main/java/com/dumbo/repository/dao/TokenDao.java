package com.dumbo.repository.dao;

import java.util.Date;

/**
 * 토큰과 관련된 CRUD 작업을 담당하는 DAO
 * 서비스(로직) 코드에서는 해당 인터페이스를 구현한 실제 구현체를 주입받아 사용
 */
public interface TokenDao 
{
    /**
     * 유저의 Refresh Token을 서버(Redis)에 저장하는 메소드
     * Refresh Token Rotation 기법을 구현하기 위해, 서버에 저장된 리프레시 토큰만을 유효한 리프레시 토큰으로 검증
     * 
     * @param userId 리프레시 토큰을 소유한 유저의 ID
     * @param refreshToken 저장할 리프레시 토큰 문자열
     * @param expDate TTL(초 단위, 리프레시 토큰의 만료 시각과 동일하게 설정)
     */
    public void saveRefreshToken(String userId, String refreshToken, Date expDate);

    /**
     * 서버에 저장된 특정 유저의 Refresh Token을 가져오는 메소드
     * 
     * @param userId 리프레시 토큰을 소유한 유저의 ID
     * 
     * @return 리프레시 토큰 문자열 / 해당 유저가 리프레시 토큰을 가지고 있지 않다면 null
     */
    public String getRefreshToken(String userId);

    /**
     * 서버에 저장된 특정 유저의 Refresh Token을 삭제하는 메소드
     * 
     * @param userId 리프레시 토큰을 소유한 유저의 ID
     */
    public void deleteRefreshToken(String userId);
}
