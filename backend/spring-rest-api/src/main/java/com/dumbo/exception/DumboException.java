package com.dumbo.exception;

public abstract class DumboException extends RuntimeException
{
    private final int statusCode;

    public DumboException(String message, int statusCode)
    {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return this.statusCode; }
}
