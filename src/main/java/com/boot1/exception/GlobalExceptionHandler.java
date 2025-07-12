package com.boot1.exception;

import java.util.Map;
import java.util.Objects;

import jakarta.validation.ConstraintViolation;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.boot1.dto.response.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final String MIN_ATTRIBUTE = "min", MAIL_TYPE = "mail";

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handlingRuntimeException() {
        return ResponseEntity.badRequest()
                .body(ApiResponse.<Void>builder()
                        .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                        .msg(ErrorCode.UNCATEGORIZED_EXCEPTION.getMsg())
                        .build());
    }

    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handlingApiException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .msg(errorCode.getMsg())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        String enumKey = "INVALID_KEY";
        for (var err : ex.getBindingResult().getAllErrors()) {
            String key = err.getDefaultMessage();
            try {
                ErrorCode.valueOf(key);
                enumKey = key;
                break;
            } catch (IllegalArgumentException ignored) {
            }
        }
        ErrorCode errorCode = ErrorCode.valueOf(enumKey);
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);
            var violation = ex.getBindingResult().getAllErrors().getFirst().unwrap(ConstraintViolation.class);
            attributes = violation.getConstraintDescriptor().getAttributes();
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.<Map<String, String>>builder()
                        .code(errorCode.getCode())
                        .msg(
                                Objects.nonNull(attributes)
                                        ? mapAttributes(errorCode.getMsg(), attributes)
                                        : errorCode.getMsg())
                        .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException() {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.<Void>builder()
                        .code(errorCode.getCode())
                        .msg(errorCode.getMsg())
                        .build());
    }

    private String mapAttributes(String message, Map<String, Object> attributes) {
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", String.valueOf(entry.getValue()));
        }
        return message;
    }
}
