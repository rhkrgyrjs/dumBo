package com.dumbo.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends DumboException
{
    public CommentNotFoundException()
    {
        super("댓글 정보를 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
    }
}
