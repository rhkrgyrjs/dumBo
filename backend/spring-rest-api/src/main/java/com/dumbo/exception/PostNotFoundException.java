package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 게시글 정보를 찾을 수 없을 때 발생하는 예외
 */
public class PostNotFoundException extends DumboException
{
    public PostNotFoundException()
    {
        super("게시글 정보를 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
    }
}
