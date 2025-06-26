package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class PostNotFoundException extends DumboException
{
    public PostNotFoundException()
    {
        super("게시글 정보를 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
    }
}
