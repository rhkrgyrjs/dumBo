package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class MissingAccessTokenException extends DumboException
{
    public MissingAccessTokenException()
    {
        super("액세스 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
