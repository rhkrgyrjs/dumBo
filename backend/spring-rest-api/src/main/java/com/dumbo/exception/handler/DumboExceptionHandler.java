package com.dumbo.exception.handler;

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
