package com.boot1.exception;

import com.boot1.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handlingRuntimeException() {
        return ResponseEntity.badRequest()
                             .body(
                                     ApiResponse.<Void>builder()
                                             .code(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode())
                                             .msg(ErrorCode.UNCATEGORIZED_EXCEPTION.getMsg())
                                                .build()
                                  );
    }
    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handlingApiException(ApiException e) {
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getStatusCode())
                             .body(
                                     ApiResponse.<Void>builder()
                                             .code(errorCode.getCode())
                                                .msg(errorCode.getMsg())
                                                .build()
                                  );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                                                               errors.put(err.getField(), err.getDefaultMessage())
                                                      );
        BindingResult bindingResult = ex.getBindingResult();
        var constraintViolations = bindingResult.getFieldErrors().getFirst().unwrap(ConstraintViolation.class);
        var attributes  = constraintViolations.getConstraintDescriptor().getAttributes();
        log.warn(attributes.toString());

        return ResponseEntity.badRequest()
                             .body(ApiResponse.<Map<String, String>>builder()
                                              .code(ErrorCode.INVALID_ERROR_CODE.getCode())
                                              .msg("Validation failed")
                                              .result(errors)
                                              .build());
    }

    @ExceptionHandler( value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handlingAccessDeniedException() {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getStatusCode())
                             .body(
                                     ApiResponse.<Void>builder()
                                                .code(errorCode.getCode())
                                                    .msg(errorCode.getMsg())
                                                    .build()
                                  );
    }
}
