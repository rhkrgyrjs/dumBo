package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 리프레시 토큰이 만료되었을 때 발생하는 예외
 */
public class RefreshTokenExpiredException extends DumboException
{
    public RefreshTokenExpiredException()
    {
        super("리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
