package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class RefreshTokenExpiredException extends DumboException
{
    public RefreshTokenExpiredException()
    {
        super("리프레시 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
