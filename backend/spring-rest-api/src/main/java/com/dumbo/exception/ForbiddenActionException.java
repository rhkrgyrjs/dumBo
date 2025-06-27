package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 권한이 없는 요청을 했을 때 발생하는 예외
 */
public class ForbiddenActionException extends DumboException
{
    public ForbiddenActionException(String message) { super(message, HttpStatus.UNAUTHORIZED.value()); }
}
