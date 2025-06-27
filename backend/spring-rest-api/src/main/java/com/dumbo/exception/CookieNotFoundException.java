package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 요청에 쿠키가 없을 때 발생하는 예외
 */
public class CookieNotFoundException extends DumboException
{
    public CookieNotFoundException()
    {
        super("쿠키가 요청에 포함되어 있지 않습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
