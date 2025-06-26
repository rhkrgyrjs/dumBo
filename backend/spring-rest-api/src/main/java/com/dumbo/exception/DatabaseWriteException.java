package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class DatabaseWriteException extends DumboException
{
    public DatabaseWriteException(String message) { super(message, HttpStatus.INTERNAL_SERVER_ERROR.value()); }
}
