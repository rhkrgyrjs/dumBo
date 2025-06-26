package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends DumboException
{
    public UserNotFoundException()
    {
        super("유저 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
    }
}
