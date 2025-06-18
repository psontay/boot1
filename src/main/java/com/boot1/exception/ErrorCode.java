package com.boot1.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    USER_EXISTS(-1,"User already exists" , HttpStatus.CONFLICT ),
    UNCATEGORIZED_EXCEPTION(-999, "UNCATEGORIZED_ERROR" , HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_INVALID(-2 , "Username must be at least 3 characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(-3 , "Pasword must be at least 6 characters" , HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(-4, "User not exists" , HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(-5, "Unauthenticated" , HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(-6 , "MAY DEO DUOC PHEP VAO!" , HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(-7 , "Role not found" , HttpStatus.NOT_FOUND),
    PERMISSION_NOT_FOUND(-8 , "Permission not found" , HttpStatus.NOT_FOUND),
    ;

    private int code;
    private String msg;
    private HttpStatusCode statusCode;
    ErrorCode(int code, String msg , HttpStatusCode statusCode) {
        this.code = code;
        this.msg = msg;
        this.statusCode = statusCode;
    }
}
