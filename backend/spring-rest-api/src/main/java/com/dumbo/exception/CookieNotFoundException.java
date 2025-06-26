package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class CookieNotFoundException extends DumboException
{
    public CookieNotFoundException()
    {
        super("쿠키가 요청에 포함되어 있지 않습니다.", HttpStatus.UNAUTHORIZED.value());
    }
}
