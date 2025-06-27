package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 액세스 토큰이 존재하지 않을 때 발생하는 예외
 */
public class MissingAccessTokenException extends DumboException
{
    public MissingAccessTokenException()
    {
        super("액세스 토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
