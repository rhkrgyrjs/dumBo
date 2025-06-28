package com.dumbo.exception;

import org.springframework.http.HttpStatus;

/**
 * 잘못된 요청 파라미터나 형식 오류 등으로 발생하는 예외
 * 상세 예외 내용은 메시지에 저장
 */
public class BadRequestException extends DumboException 
{
    public BadRequestException(String message) { super(message, HttpStatus.BAD_REQUEST.value()); }
}
