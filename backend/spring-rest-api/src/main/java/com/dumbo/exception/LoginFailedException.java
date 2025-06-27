package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 로그인에 실패했을 때 발생하는 예외
 */
public class LoginFailedException extends DumboException
{
    public LoginFailedException()
    {
        super("로그인에 실패했습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
