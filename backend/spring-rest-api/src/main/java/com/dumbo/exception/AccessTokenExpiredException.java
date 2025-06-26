package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class AccessTokenExpiredException extends DumboException
{
    public AccessTokenExpiredException()
    {
        super("액세스 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
