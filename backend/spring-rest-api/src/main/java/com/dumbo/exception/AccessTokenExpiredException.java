package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 액세스 토큰이 만료되었을 때 발생하는 예외
 */
public class AccessTokenExpiredException extends DumboException
{
    public AccessTokenExpiredException()
    {
        super("액세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
