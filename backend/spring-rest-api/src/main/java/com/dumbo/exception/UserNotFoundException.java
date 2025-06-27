package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 유저 정보를 찾을 수 없을 때 발생하는 예외
 */
public class UserNotFoundException extends DumboException
{
    public UserNotFoundException()
    {
        super("유저 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
    }
}
