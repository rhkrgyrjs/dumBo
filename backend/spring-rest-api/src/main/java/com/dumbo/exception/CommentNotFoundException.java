package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 댓글을 찾을 수 없을 때 발생하는 예외
 */
public class CommentNotFoundException extends DumboException
{
    public CommentNotFoundException()
    {
        super("댓글 정보를 정보를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value());
    }
}
