package com.dumbo.exception;

/**
 * 컨트롤러 내부(서비스 로직)에서 발생 가능한 오류
 * 에러 내용과 HTTP 코드를 포함
 * 상세 내용을 저장할 경우 이 클래스를 상속해 예외를 선언함
 */
public abstract class DumboException extends RuntimeException
{
    private final int statusCode;

    public DumboException(String message, int statusCode)
    {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() { return this.statusCode; }
}
