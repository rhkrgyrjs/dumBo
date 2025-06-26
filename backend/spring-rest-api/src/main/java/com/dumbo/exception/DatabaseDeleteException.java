package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class DatabaseDeleteException extends DumboException
{
    public DatabaseDeleteException(String message) { super(message, HttpStatus.INTERNAL_SERVER_ERROR.value()); }
}
