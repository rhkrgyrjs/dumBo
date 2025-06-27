package com.dumbo.exception.handler;

/*
 * DumboExceptionHandler
 * 
 * 컨트롤러에서 발생하는 에러를 처리하는 전역 예외 처리 객체
 * RuntimeException을 상속한 DumboException을 처리 : DumboException은 예외 사유와 HTTP code 포함
 * 실제 발생하는 예외들은 DumboException을 상속한 예외들
 */

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.dumbo.exception.DumboException;

@RestControllerAdvice
public class DumboExceptionHandler 
{
    @ExceptionHandler(DumboException.class)
    public ResponseEntity<Map<String, String>> handleMissingAccessToken(DumboException e)
    {
        return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getMessage()));
    }
}
