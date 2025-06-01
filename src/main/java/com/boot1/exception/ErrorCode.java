package com.boot1.exception;

public enum ErrorCode {
    USER_EXISTS(-1,"User already exists"),
    UNCATEGORIZED(-999,"UNCATEGORIZED_ERROR"),
    USERNAME_INVALID(-2 , "Username must be at least 3 characters"),
    PASSWORD_INVALID(-3 , "Pasword must be at least 6 characters"),
    USER_NOT_EXISTS(-4, "User not exists"),
    ;

    private int code;
    private String msg;
    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
