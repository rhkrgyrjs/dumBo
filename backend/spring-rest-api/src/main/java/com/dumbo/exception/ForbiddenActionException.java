package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenActionException extends DumboException
{
    public ForbiddenActionException(String message) { super(message, HttpStatus.UNAUTHORIZED.value()); }
}
