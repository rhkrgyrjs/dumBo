package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class DatabaseReadException extends DumboException
{
    public DatabaseReadException(String message) { super(message, HttpStatus.INTERNAL_SERVER_ERROR.value()); }
}
