package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 데이터를 삭제할 때 발생하는 예외
 * 상세 예외 내용은 메시지에 저장
 */
public class DatabaseDeleteException extends DumboException
{
    public DatabaseDeleteException(String message) { super(message, HttpStatus.INTERNAL_SERVER_ERROR.value()); }
}
