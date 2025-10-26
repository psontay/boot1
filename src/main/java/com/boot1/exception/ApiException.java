package com.boot1.exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class ApiException extends RuntimeException {
    ErrorCode errorCode;
    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public ApiException(ErrorCode errorCode, String detailMsg) {
        super(detailMsg);
        this.errorCode = errorCode;
    }
}
