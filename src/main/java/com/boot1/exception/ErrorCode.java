package com.boot1.exception;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public enum ErrorCode {
    USER_EXISTS(-1,"User already exists" , HttpStatus.CONFLICT ),
    UNCATEGORIZED_EXCEPTION(-999, "UNCATEGORIZED_ERROR" , HttpStatus.INTERNAL_SERVER_ERROR),
    USERNAME_LENGTH_INVALID(-2 , "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(-3 , "Password must be at least {min} characters" , HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTS(-4, "User not exists" , HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(-5, "Unauthenticated" , HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(-6 , "MAY DEO DUOC PHEP VAO!" , HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(-7 , "Role not found" , HttpStatus.NOT_FOUND),
    PERMISSION_IS_EMPTY(-10 , "Permission is empty" , HttpStatus.CONFLICT),
    PERMISSION_NOT_FOUND(-8 , "Permission not found" , HttpStatus.NOT_FOUND),
    PERMISSION_EXISTS(-9 , "Permission already exists" , HttpStatus.CONFLICT),
    ROLE_EXISTS(-10 , "Role already exists" , HttpStatus.CONFLICT),
    INVALID_DATE_OF_BIRTH(-11 , "Invalid date of birth , must be at least {min} years old" , HttpStatus.BAD_REQUEST),
    INVALID_ERROR_CODE(-12 , "Invalid error code" , HttpStatus.BAD_REQUEST),
    INVALID_KEY(-13 , "Invalid key" , HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_TYPE(-14 , "Invalid email type , must start with {domain} & end with @gmail.com" , HttpStatus.BAD_REQUEST),
    USERNAME_TYPE_INVALID(-15 , "Username must be at least 1 character upper" , HttpStatus.BAD_REQUEST),
    PASSWORD_TYPE_INVALID(-16 , "Password must be at least 1 character upper" , HttpStatus.BAD_REQUEST),
    FIRST_NAME_EMPTY(-17 , "First name is empty" , HttpStatus.BAD_REQUEST),
    LAST_NAME_EMPTY(-18 , "Last name is empty" , HttpStatus.BAD_REQUEST),
    EMAIL_EMPTY(-19 , "Email is empty" , HttpStatus.BAD_REQUEST),
    ;

    int code;
    String msg;
    HttpStatusCode statusCode;
    ErrorCode(int code, String msg , HttpStatusCode statusCode) {
        this.code = code;
        this.msg = msg;
        this.statusCode = statusCode;
    }
}
