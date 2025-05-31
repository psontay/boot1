package com.example.boot1.exception;

public class ApiException extends RuntimeException {
    private final ErrorCode errorCode;
    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }
    public ErrorCode getErrorCode() { return errorCode; }
}
